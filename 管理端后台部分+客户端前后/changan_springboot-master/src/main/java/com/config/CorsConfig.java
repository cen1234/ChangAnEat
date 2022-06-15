package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    //当前跨域请求的最大有效时长 这里默认是一天
    private static final long MAX_AGE = 24 * 60 * 60;

    private CorsConfiguration buildConfig(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        //corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("*");//1、设置访问源地址
        corsConfiguration.addAllowedMethod("*");//2、设置访问源请求头
        corsConfiguration.addAllowedHeader("*");//3、设置访问源请求方法
        corsConfiguration.setMaxAge(MAX_AGE);
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", buildConfig());//4、对接口配置跨域设置
        return new CorsFilter(configSource);
    }

}
