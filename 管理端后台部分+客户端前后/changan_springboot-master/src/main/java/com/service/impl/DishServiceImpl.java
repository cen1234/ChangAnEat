package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.DeleteException;
import com.dto.DishDto;
import com.entity.Category;
import com.entity.Dish;
import com.entity.DishFlavor;
import com.entity.SetMealDish;
import com.mapper.DishMapper;
import com.service.CategoryService;
import com.service.DishFlavorService;
import com.service.DishService;
import com.service.SetMealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Resource
    private DishFlavorService dishFlavorService;//注入菜品对应的口味表

    @Resource
    private SetMealDishService setMealDishService;


    /**
     * 新增菜品 并且一个菜品有多个口味
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto){
        //保存菜品的基本信息 到 dish表中
        this.save(dishDto);

        //保存菜品对应的口味表 dish_flavor
        Long id = dishDto.getId();//菜品id

        //将菜品id注入到dish_flavor中
        List<DishFlavor> flavorList = dishDto.getFlavor();
        for(DishFlavor flavor : flavorList){
            flavor.setDishId(id);
        }

        dishFlavorService.saveBatch(dishDto.getFlavor());//默认并没有封装菜品的ID 所以上述需要手动注入菜品的id
    }

    /**
     * 得到所有的信息 并 封装到dto中
     * @param id
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品的信息
        Dish dish = this.getById(id);
        //返回的dto类型
        DishDto dishDto = new DishDto();
        //拷贝
        BeanUtils.copyProperties(dish,dishDto);

        //查询菜品对应的口味表的信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        //将查询结果封装到dto中
        dishDto.setFlavor(flavors);//封装口味

        return dishDto;
    }

    /**
     * 更新菜品表 重构菜品口味表
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {

        //1、更新菜品表
        log.info("更新菜品表");
        this.updateById(dishDto);

        //2、清空菜品对应的口味表 并 重新进行添加
        log.info("清空口味表");
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);

        //3、将新的口味添加到口味表
        log.info("添加新口味");
        List<DishFlavor> flavors = dishDto.getFlavor();
        for(DishFlavor dishFlavor : flavors){
            dishFlavor.setDishId(dishDto.getId());//加入id值
        }
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品 以及 菜品表
     * @param ids
     */
    @Override
    public void removeWithFlavor(List<Long> ids) {
        //构造当前商品的查询条件 id值在ids中 并且 状态值为1的数据
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(Dish::getId,ids);
        dishLambdaQueryWrapper.eq(Dish::getStatus,1);

        //如果个数大于0 就证明有售卖中的商品所以不能被删除
        int dishCount = this.count(dishLambdaQueryWrapper);
        if(dishCount > 0){
            throw new DeleteException("该菜品正在售卖不能被删除");
        }

        //构造套餐菜品对应表的查询条件 菜品对应的id值在ids中
        LambdaQueryWrapper<SetMealDish> setMealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setMealDishLambdaQueryWrapper.in(SetMealDish::getDishId,ids);
        int setMealCount = setMealDishService.count(setMealDishLambdaQueryWrapper);
        if(setMealCount > 0){
            throw new DeleteException("该商品关联套餐不能被删除");
        }

        //删除菜品表
        this.removeByIds(ids);

        //菜品对应的口味表
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
    }
}
