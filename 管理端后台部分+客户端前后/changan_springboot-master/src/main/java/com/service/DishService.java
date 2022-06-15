package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dto.DishDto;
import com.entity.Dish;

import java.util.IdentityHashMap;
import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品 以及 菜品对应的口味表
    void saveWithFlavor(DishDto dishDto);

    //根据id查出菜品 以及 菜品口味表对应的所有信息
    DishDto getByIdWithFlavor(Long id);

    //更新菜品 以及菜品对应的口味表
    void updateWithFlavor(DishDto dishDto);

    void removeWithFlavor(List<Long> ids);
}
