package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.BaseContext;
import com.common.R;
import com.dto.UserDto;
import com.entity.User;
import com.service.UserService;
import com.utils.MailUtil;
import com.utils.ValidateCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private MailUtil mailUtil;//发送邮件的工具类

    @Resource
    private RedisTemplate redisTemplate;//redis操作对象

    @Resource
    private UserService userService;//数据库操作对象

    /**
     * 邮箱发送验证码 并存入redis
     * @param
     * @return
     */
    @PostMapping("/sendMail")
    public R<String> sendMail(@RequestBody UserDto userDto){
        //随机生成四位验证码
        String code = ValidateCodeUtil.generateValidateCode(4).toString();

        log.info("验证码为:" + code);

        //向对应的邮箱发送验证码
        if(mailUtil.sendMail(userDto.getMail(),code)){
            //将生成的验证码缓存到redis中 并设置有效时间为五分钟
            redisTemplate.opsForValue().set(userDto.getMail(),code,5, TimeUnit.MINUTES);

            return R.success("已给该邮箱成功发送四位验证码 请查收");
        }
        return R.error("发送失败");
    }

    /**
     * 邮箱验证和密码登录
     * @param userDto
     * @return
     */
    @PostMapping("/login")
    public R<?> login(HttpServletRequest request, @RequestBody UserDto userDto){
        log.info(userDto.getMail() + "-" + userDto.getCode() + "-" + userDto.getPassword());
        //判断是否为新用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getMail,userDto.getMail());
        User user = userService.getOne(queryWrapper);

        //邮箱密码登录
        if(userDto.getCode() == null && userDto.getPassword() != null){

            //加密
            String password = DigestUtils.md5DigestAsHex(userDto.getPassword().getBytes());//参数是byte数组

            //首次登录直接默认密码
            if(user == null){
                user = new User();
                user.setMail(userDto.getMail());
                user.setStatus(1);
                user.setPassword(password);//将加密之后的密码存入数据库
                userService.save(user);

                BaseContext.setCurrentId(user.getId());

                return R.success(user);
            }
            //不是首次登录 比较密码
            else{
                if(password.equals(user.getPassword())){

                    BaseContext.setCurrentId(user.getId());

                    return R.success(user);
                }
                return R.error("密码错误");
            }
        }

        //验证码登录
        else if(userDto.getCode() != null && userDto.getPassword() == null){
            //从redis中取出验证码
            Object codeInRedis = redisTemplate.opsForValue().get(userDto.getMail());

            //验证码输入正确
            if(codeInRedis != null && codeInRedis.equals(userDto.getCode())){
                //首次登录 存入数据库
                if(user == null){
                    user = new User();
                    user.setMail(userDto.getMail());
                    user.setStatus(1);
                    userService.save(user);
                }

                BaseContext.setCurrentId(user.getId());

                request.getSession().setAttribute("userId",user.getId());

                //如果用户登录成功 删除redis中缓存的验证码
                redisTemplate.delete(user.getMail());

                return R.success(user);
            }
        }
        return R.error("登陆失败");
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().setAttribute("userId",null);
        return R.success("退出登录成功");
    }

}
