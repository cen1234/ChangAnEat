package com.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用的返回结果类【目的是返回给客户端的数据都会封装为该对象】
 * 使用了泛型【主要用于结果】
 * @param <T>
 */
@Data//会为类的所有属性自动生成setter/getter、equals、canEqual、hashCode、toString方法 不会生成无参 此处有无参构造是因为默认生成
public class R<T> implements Serializable {

    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据

    private Map map = new HashMap(); //动态数据

    //成功方法复制
    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    //失败方法赋值
    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }


    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
