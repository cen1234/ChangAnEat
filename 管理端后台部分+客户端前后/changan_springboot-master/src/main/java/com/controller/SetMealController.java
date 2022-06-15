package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.R;
import com.dto.SetMealDto;
import com.entity.SetMeal;
import com.service.CategoryService;
import com.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setMeal")
public class SetMealController {
    @Resource
    private SetMealService setMealService;

    @Resource
    private CategoryService categoryService;

    //ip地址+端口号+项目名称
    private static final String PATH = "http://" + "192.168.0.80" + ":9090" + "/changAn/img/";

    /**
     * 新增套餐【操作套餐表 以及套餐与菜品的对应关系表】
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setMealCache",allEntries = true)
    public R<String> save(@RequestBody SetMealDto setMealDto){
        log.info(setMealDto.toString());

        setMealService.saveWithDish(setMealDto);//自定义的方法 保存了两张表的数据

        return R.success("新增套餐成功");
    }

    /**setMealDto = {SetMealDto@9106} "SetMealDto(setMealDishList=null, categoryName=null)"
     * 分页展示套餐的详情
     * @param page
     * @param pageSize
     * @param search
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetMealDto>> page(int page, int pageSize, String search){
        //构造分页构造器对象
        Page<SetMeal> pageInfo = new Page<>(page,pageSize);
        Page<SetMealDto> pageDtoInfo = new Page<>();//必须先初始化为page类型

        //条件构造器
        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(search),SetMeal::getName,search);//如果有搜索名称 则进行模糊查询

        //按照更新时间降序排序
        queryWrapper.orderByDesc(SetMeal::getUpdateTime);

        //进行分页查询
        setMealService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,pageDtoInfo,"records");

        //获取返回的数据集合
        List<SetMeal> records = pageInfo.getRecords();

        //转换成dto类型
        List<SetMealDto> setMealDtoList = records.stream().map((item) -> {
            //创建每一个dto对象 并 拷贝其余的属性
            SetMealDto setMealDto = new SetMealDto();
            BeanUtils.copyProperties(item, setMealDto);

            //给每一个dto加入 根据查询得到对应的缺失的分类名称属性
            String categoryName = categoryService.getById(item.getCategoryId()).getName();
            setMealDto.setCategoryName(categoryName);

            //给照片加上ip和port
            String img = PATH + setMealDto.getImage();
            setMealDto.setImage(img);

            //返回dto
            return setMealDto;
        }).collect(Collectors.toList());

        //赋值records属性
        pageDtoInfo.setRecords(setMealDtoList);

        return R.success(pageDtoInfo);//返回的属性一定是dto类型的
    }

    /**
     * 根据id获取所有的套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetMealDto> get(@PathVariable Long id){
        SetMealDto setMealDto = setMealService.getByIdWithDish(id);

        return R.success(setMealDto);
    }

    /**
     * 批量或逐个删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping("/{ids}")
    @CacheEvict(value = "setMealCache",allEntries = true)//true表示清理setMeal中所有的缓存数据
    public R<String> delete(@PathVariable List<Long> ids){
        setMealService.removeWithDish(ids);
        return R.success("套餐删除成功");
    }

    /**
     * 批量或者逐个修改套餐的状态
     * @param id
     * @param status
     * @return
     */
    @PutMapping("/status/{status}")
    @CacheEvict(value = "setMealCache",allEntries = true)
    public R<String> updateStatus(@RequestBody List<String> id,@PathVariable int status){
        //将字符串转化你为Long
        List<Long> ids = new ArrayList<>();
        for(String s : id){
            ids.add(Long.valueOf(s));
        }

        //更新构造器
        UpdateWrapper<SetMeal> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(SetMeal::getId,ids);//条件是 id在ids中
        updateWrapper.lambda().set(SetMeal::getStatus,status);//将当前状态改到统一的状态
        setMealService.update(updateWrapper);//执行更新

        return R.success("套餐状态修改成功");
    }


    /**
     * 根据套餐的id查询出套餐的具体数据
     * @param setMeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setMealCache",key = "#setMeal.categoryId")//key为套餐的分类id 将方法的返回加过放到redis中
    public R<List<SetMeal>> list(SetMeal setMeal){
        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setMeal.getCategoryId() != null,SetMeal::getCategoryId,setMeal.getCategoryId());//根据套餐的分类id 查出所有的套餐数量
        queryWrapper.eq(SetMeal::getStatus,1);//未售状态的不查询
        queryWrapper.orderByDesc(SetMeal::getUpdateTime);//按照更新时间降序


        List<SetMeal> list = setMealService.list(queryWrapper);

        for(SetMeal meal : list){
            String img = meal.getImage();
            img = PATH + img;
            meal.setImage(img);
        }

        return R.success(list);
    }

    /**
     * 修改套餐和套餐对应的菜品表
     * @param setMealDto
     * @return
     */
    @PutMapping
    @CacheEvict(value = "setMealCache",allEntries = true)
    public R<String> update(@RequestBody SetMealDto setMealDto){
        log.info("修改套餐");

        setMealService.updateByIdWithDish(setMealDto);
        
        return R.success("修改成功");
    }


    @GetMapping("/dish/{id}")
    public R<SetMeal> getSetMeal(@PathVariable Long id){
        SetMeal setMeal = setMealService.getById(id);



        String img = setMeal.getImage();
        img = PATH + img;
        setMeal.setImage(img);

        return R.success(setMeal);
    }

}
