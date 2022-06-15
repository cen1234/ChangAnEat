package com.dto;

import com.entity.Dish;
import com.entity.DishFlavor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish implements Serializable {
    //扩展菜品表 添加 菜品对应的分类
    private List<DishFlavor> flavor = new ArrayList<>();//关联的是菜品口味表 注：这里就要求参数必须是flavor 每一条都必须是name和value

    //扩展菜品表 显示对应菜品的分类名称【本来数据库存放的是菜品分类的id而不是名称】
    private String categoryName;//关联的是菜品分类表
}
