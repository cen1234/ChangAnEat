package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.DeleteException;
import com.entity.Category;
import com.entity.Dish;
import com.entity.SetMeal;
import com.mapper.CategoryMapper;
import com.service.CategoryService;
import com.service.DishService;
import com.service.SetMealService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.Serializable;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Resource
    private DishService dishService;

    @Resource
    private SetMealService setMealService;

    /**
     * 判断该分类是否关联菜品或者套餐
     * @param id 分类的id
     * @return
     */
    @Override
    public boolean removeById(Serializable id) {
        //查询当前分类是否关联菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int dishCount = dishService.count(dishLambdaQueryWrapper);
        if(dishCount != 0){
            //该商品不能删除
            throw new DeleteException("该分类关联某个菜品 不能被删除");
        }

        //查询当前分类是否关联套餐
        LambdaQueryWrapper<SetMeal> setMealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setMealLambdaQueryWrapper.eq(SetMeal::getCategoryId,id);
        int setMealCount = setMealService.count(setMealLambdaQueryWrapper);
        if(setMealCount != 0){
            //该分类不能删除
            throw new DeleteException("该分类关联套餐 不能被删除");
        }

        return super.removeById(id);
    }
}
