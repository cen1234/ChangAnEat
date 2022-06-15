package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.DeleteException;
import com.dto.DishDto;
import com.dto.SetMealDto;
import com.entity.SetMeal;
import com.entity.SetMealDish;
import com.mapper.SetMealMapper;
import com.service.SetMealDishService;
import com.service.SetMealService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;

@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, SetMeal> implements SetMealService {

    @Resource
    private SetMealDishService setMealDishService;

    /**
     * 新增套餐 同时增加套餐和菜品的关联关系
     * @param setMealDto
     */
    @Transactional//事务
    @Override
    public void saveWithDish(SetMealDto setMealDto) {
        //保存套餐表的信息
        this.save(setMealDto);

        //保存套餐_菜品 的关系表
        List<SetMealDish> setMealDishList = setMealDto.getSetMealDishList();

        if(setMealDishList != null) {
            for (SetMealDish dish : setMealDishList) {
                dish.setSetMealId(setMealDto.getId());//将套餐的id值存入
            }
        }
        setMealDishService.saveBatch(setMealDishList);
    }

    /**
     * 删除套餐 同时删除套餐和菜品的关联关系
     * @param ids
     */
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        //查询套餐的状态 判断是否有在售的 如果在售 则不能删除
        LambdaQueryWrapper<SetMeal>  setMealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setMealLambdaQueryWrapper.in(SetMeal::getId,ids);
        setMealLambdaQueryWrapper.eq(SetMeal::getStatus,1);

        int count = this.count(setMealLambdaQueryWrapper);
        if(count > 0){
            throw new DeleteException("该套餐正在售卖 不能被删除！");
        }

        //删除关系表中的数据
        LambdaQueryWrapper<SetMealDish> setMealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setMealDishLambdaQueryWrapper.eq(SetMealDish::getSetMealId,ids);
        setMealDishService.remove(setMealDishLambdaQueryWrapper);

        //删除套餐表中的数据
        this.removeByIds(ids);
    }


    /**
     * 根据id查询所有的信息
     * @param id
     * @return
     */
    @Override
    public SetMealDto getByIdWithDish(Long id) {
        //查出套餐表的信息
        SetMeal meal = this.getById(id);

        //将套餐表的信息封装到dto中
        SetMealDto setMealDto = new SetMealDto();
        BeanUtils.copyProperties(meal,setMealDto);

        //找出与套餐名称相同的 套餐菜品表
        LambdaQueryWrapper<SetMealDish> setMealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setMealDishLambdaQueryWrapper.eq(SetMealDish::getSetMealId,id);
        List<SetMealDish> list = setMealDishService.list(setMealDishLambdaQueryWrapper);
        setMealDto.setSetMealDishList(list);//封装到dto中

        //返回所有的信息
        return setMealDto;
    }

    /**
     * 修改信息
     * @param setMealDto
     */
    @Override
    public void updateByIdWithDish(SetMealDto setMealDto){
        //保存基本数据
        this.updateById(setMealDto);

        //删除该套餐所有的口味数据
        LambdaQueryWrapper<SetMealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetMealDish::getSetMealId,setMealDto.getId());
        setMealDishService.remove(queryWrapper);

        //查询id并进行口味表的保存
        List<SetMealDish> setMealDishList = setMealDto.getSetMealDishList();
        if(setMealDishList != null) {
            for (SetMealDish dish : setMealDishList) {
                dish.setSetMealId(setMealDto.getId());//将套餐的id值存入
            }
        }

        //保存
        setMealDishService.saveBatch(setMealDishList);
    }
}
