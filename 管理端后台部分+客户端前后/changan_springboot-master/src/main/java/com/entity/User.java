package com.entity;

import lombok.Data;

@Data
public class User {
    private Long id;

    //姓名
    private String name;


    //邮箱
    private String mail;

    //密码
    private String password;


    //性别 0 女 1 男
    private String sex;


    //身份证号
    private String idNumber;


    //头像
    private String avatar;


    //状态 0:禁用，1:正常
    private Integer status;
}
