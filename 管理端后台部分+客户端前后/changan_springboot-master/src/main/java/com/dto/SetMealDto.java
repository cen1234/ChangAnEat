package com.dto;

import com.entity.SetMeal;
import com.entity.SetMealDish;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SetMealDto extends SetMeal implements Serializable {

    //套餐对应的菜品
    private List<SetMealDish> setMealDishList;

    //套餐对应的分类名称【只存入了套餐分类的id】
    private String categoryName;
}
