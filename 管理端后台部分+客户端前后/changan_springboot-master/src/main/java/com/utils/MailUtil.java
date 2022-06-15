package com.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;

@Slf4j
@Component
public class MailUtil {
    /**
     * springboot专门发送邮件接口
     */
    @Resource
    private JavaMailSenderImpl javaMailSender;

    public boolean sendMail(String mail, String data){//转为json格式
        log.info("开始发送邮件");
        SimpleMailMessage msg = new SimpleMailMessage();
        //发送邮件的邮箱
        msg.setFrom("1474250484@qq.com");
        //发送到哪(邮箱)
        msg.setTo(mail);
        //邮箱标题
        msg.setSubject("长安十二食辰");
        //邮箱文本
        msg.setText("【长安十二食辰】您的验证码为:" + data + "\n请您妥善保管并尽快登录");
        try {
            javaMailSender.send(msg);
        } catch (MailException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        return true;
    }
}
