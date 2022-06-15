package com.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@Slf4j
public class MailConfiguration {
    @Bean
    public JavaMailSenderImpl JavaMailSender(){

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.qq.com");
        mailSender.setUsername("1474250484@qq.com");
        mailSender.setPassword("chohhhrfgjsuheji");
        return mailSender;
    }
}
