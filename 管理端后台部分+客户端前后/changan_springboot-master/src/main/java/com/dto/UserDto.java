package com.dto;

import com.entity.User;
import lombok.Data;

@Data
public class UserDto extends User {
    String code;//只用来接收参数
}
