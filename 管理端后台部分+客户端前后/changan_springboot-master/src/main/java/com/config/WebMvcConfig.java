package com.config;

import com.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 设置静态资源映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**").addResourceLocations("classpath:/img/");

        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * 扩展MVC的消息转换器
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的转换器对象 追加到MVC框架的转换器集合中
        converters.add(0,messageConverter);//优先级设为最高
    }

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        //addPathPatterns拦截的路径
//        String[] addPathPatterns = {"/**"};
//
//        //excludePathPatterns排除的路径
//        String[] excludePathPatterns = {"/employee/login","/user/login","/user/sendMail"};
//
//        //创建用户拦截器对象并指定其拦截的路径和排除的路径
//        registry.addInterceptor(new EmployeeHandlerInterceptor()).addPathPatterns().excludePathPatterns(excludePathPatterns);
//    }
}
