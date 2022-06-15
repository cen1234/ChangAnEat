package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dto.SetMealDto;
import com.entity.SetMeal;

import java.util.List;

public interface SetMealService extends IService<SetMeal> {
    void saveWithDish(SetMealDto setMealDto);

    void removeWithDish(List<Long> ids);

    SetMealDto getByIdWithDish(Long id);

    void updateByIdWithDish(SetMealDto setMealDto);
}
