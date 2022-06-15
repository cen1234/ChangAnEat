package com.common;

/**
 * 为什么时运行时异常
 *      因为运行时异常不用被捕获
 *      不需要处理 只需要在全局处理异常类中处理即可
 * 删除分类有关联时抛出异常
 * 删除套餐在售出时抛出异常
 * 删除菜品
 */
public class DeleteException extends RuntimeException{
    public DeleteException(String message){
        super(message);
    }
}
