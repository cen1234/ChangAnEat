package com.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 处理主键冲突
     * @return
     */
    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public R<String> SqlIntegrityConstraintViolationExceptionHandler(SQLIntegrityConstraintViolationException e){
        log.error(e.getMessage());

        if(e.getMessage().contains("Duplicate entry")){
            String[] split = e.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }

        return R.error("数据库操作失败");
    }

    /**
     * 处理未知的sql异常
     * @param e
     * @return
     */
    @ExceptionHandler({SQLException.class})
    public R<String> SqlExceptionHandler(SQLException e){
        log.error(e.getMessage());
        return R.error("数据库操作失败");
    }

    /**
     * 处理删除分类抛出的异常
     * @param e
     * @return
     */
    @ExceptionHandler({DeleteException.class})
    public R<String> deleteCategoryEHandler(DeleteException e){
        log.error(e.getMessage());
        return R.error(e.getMessage());
    }
}
