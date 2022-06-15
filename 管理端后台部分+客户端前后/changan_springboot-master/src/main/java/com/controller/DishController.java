package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.R;
import com.dto.DishDto;
import com.entity.Category;
import com.entity.Dish;
import com.entity.DishFlavor;
import com.service.CategoryService;
import com.service.DishFlavorService;
import com.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    //ip地址+端口号+项目名称
    private static final String PATH = "http://" + "192.168.0.80" + ":9090" + "/changAn/img/";

    @Resource
    private DishService dishService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "dishCache",allEntries = true)
    public R<String> addDish(@RequestBody DishDto dishDto){//此处传值需要菜品菜品分类的id值 口味的名称是name 口味对应的列表是value
        log.info(dishDto.toString());

        //保存菜品的基本信息到菜品表dish 以及 菜品口味表dish_flavor中
        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 分页显示菜品
     * @param page
     * @param pageSize
     * @param search
     * @return
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String search){

        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> pageDtoInfo = new Page<>();//必须先初始化为page类型

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(search),Dish::getName,search);//如果有搜索名称 则进行模糊查询

        //按照更新时间降序排序
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //进行分页查询
        dishService.page(pageInfo,queryWrapper);

        /**
         * 对象拷贝：
         *      拷贝的属性只有数据的总个数total等其他的属性
         *      records不能被直接拷贝 首先records是list集合的类型 集合中的每一条数据都必须是dto扩展类型的 否则无法存入属性名称
         */
        BeanUtils.copyProperties(pageInfo,pageDtoInfo,"records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> dtoRecords = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();//每一个dto对象
            BeanUtils.copyProperties(item,dishDto);//先拷贝属性

            //根据分类的id查出对应的那一行数据 再找出分类的名称
            Category category = categoryService.getById(item.getCategoryId());
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //修改image的属性 为其加上ip和端口号
            String img = PATH + dishDto.getImage();
            dishDto.setImage(img);

            return dishDto;
        }).collect(Collectors.toList());

        //将records的值传入page中
        pageDtoInfo.setRecords(dtoRecords);

        return R.success(pageDtoInfo);//返回的属性一定是dto类型的
    }

    /**
     * 根据菜品的id查询对应的口味信息【在修改时作为数据回显】
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable String id){
        log.info("菜品需要回显的信息");

        DishDto dishDto =  dishService.getByIdWithFlavor(Long.valueOf(id));

        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    @CacheEvict(value = "dishCache",allEntries = true)
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);//具体逻辑在service

        return R.success("修改菜品成功");
    }


    /**
     * 批量删除 或者 单个删除
     * @param ids
     * @return
     */
    @DeleteMapping("/{ids}")
    @CacheEvict(value = "dishCache",allEntries = true)
    public R<String> delete(@PathVariable("ids") List<Long> ids){
        dishService.removeWithFlavor(ids);

        return R.success("菜品删除成功");
    }

    /**
     * 批量修改状态 或者 单个修改状态【起售或者停售】
     * @param id
     * @param status
     * @return
     */
    @PutMapping("/status/{status}")
    @CacheEvict(value = "dishCache",allEntries = true)
    public R<String> updateStatus(@RequestBody List<String> id,@PathVariable int status){
        log.info(String.valueOf(id));

        //修改状态
//        status = status == 1 ? 0 : 1;//前端已经修改好了状态 所以此处不需要修改

        //将字符串转化你为Long
        List<Long> ids = new ArrayList<>();
        for(String s : id){
            ids.add(Long.valueOf(s));
        }

        //更新构造器
        UpdateWrapper<Dish> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(Dish::getId,ids);//条件是 id在ids中
        updateWrapper.lambda().set(Dish::getStatus,status);//将当前状态改到统一的状态
        dishService.update(updateWrapper);//执行更新

        return R.success("状态修改成功");
    }


    /**
     * 根据菜品的分类id查出它的所有菜品信息以及口味表
     * @param dish
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "dishCache",key = "#dish.categoryId")
    public R<List<DishDto>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //查询等于当前传过来的分类id 的各个菜品
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.like(StringUtils.isNotEmpty(dish.getName()),Dish::getName,dish.getName());

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        //查询出菜品
        List<Dish> list = dishService.list(queryWrapper);

        //根据菜品的id值查出对应的口味信息 封装到dto中
        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();//每一个dto对象

            BeanUtils.copyProperties(item,dishDto);//拷贝

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);//口味表中的dishId等于当前菜品id的所有数据
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavor(dishFlavorList);//封装

            //修改image的属性 为其加上ip和端口号
            String img = PATH + dishDto.getImage();
            dishDto.setImage(img);

            //返回每一个dto对象
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }


    @GetMapping("/search")
    public R<List<DishDto>> search(String search){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Dish::getName,search);
        List<Dish> dishList = dishService.list(queryWrapper);
        if(dishList == null){
            R.error("没有查到数据");
        }
        List<DishDto> dishDtoList = dishList.stream().map((item) -> {
            DishDto dishDto = new DishDto();//每一个dto对象
            BeanUtils.copyProperties(item,dishDto);//拷贝

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);//口味表中的dishId等于当前菜品id的所有数据
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavor(dishFlavorList);//封装

            //修改image的属性 为其加上ip和端口号
            String img = PATH + dishDto.getImage();
            dishDto.setImage(img);

            //返回每一个dto对象
            return dishDto;
        }).collect(Collectors.toList());


        return R.success(dishDtoList);
    }
}
