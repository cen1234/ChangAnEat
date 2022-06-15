package com.interceptor;

import com.alibaba.fastjson.JSON;
import com.common.BaseContext;
import com.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class EmployeeHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("拦截该请求:" + request.getRequestURI());

        Long id = BaseContext.getCurrentId();

        log.info("该请求的id是:" + id);

        if(id != null){
            log.info("用户已登录，用户id为：{}",id);

            BaseContext.setCurrentId(id);//线程中存入id值

            return true;
        }

        else {
            response.getWriter().write(JSON.toJSONString(R.error("NOT_LOGIN")));
            return false;
        }
    }

}
