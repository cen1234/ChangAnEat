package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.BaseContext;
import com.common.R;
import com.entity.Employee;
import com.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j//日志
@RestController//相当于ResponseBody+Controller 遇到请求之后会扫描带有controller的页面 而 不加ResponseBody返回的是页面 加了之后返回的是json的数据格式 所以不用在控制器上加ResponseBody
@RequestMapping("/employee")
public class EmployeeController {

    @Resource//自动注入 默认使用byName 如果byName赋值失败再使用byType
    private EmployeeService employeeService;

    @Resource
    private RedisTemplate redisTemplate;//redis操作对象

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login") //相当于是@RequestMapping(method = RequestMethod.POST)
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee){//RequestBody:将传过来的json格式的对象封装成对应的实体类 需要参数名和传入的参数相同
        log.info("username:{} password:{}",employee.getUsername(),employee.getPassword());
        System.out.println("EmployeeController sessionid:" + request.getSession().getId());

        //1、将密码使用md5进行加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());//参数是byte数组

        //2、根据username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);//唯一性约束

        //3、判断是否查到员工信息 如果不为null则查出来的是该账号对应的正确的密码
        if(emp == null){
            return R.error("该员工不存在");
        }

        //4、正确密码和输入的密码进行比对
        if(!emp.getPassword().equals(password)){
            //如果不成功
            return R.error("该账号密码错误");
        }

        //5、查看员工状态 如果为已禁用状态 则返回员工已禁用的结果
        if(emp.getStatus() == 0){
            return R.error("该员工账号已禁用");
        }

        //6、登录成功 将主键id存入session
//        request.getSession().setAttribute("employee",emp.getId());

        Long id = emp.getId();
        BaseContext.setCurrentId(id);

        return R.success(emp);//返回给前端 前端放到浏览器缓存中使用
    }


    /**
     * 员工退出登录
     * @param request
     * @return
     */
    @PostMapping("/exit")
    public R<String> logout(HttpServletRequest request){
        //清除session中的存储的员工id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }


    /**
     * 添加员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));//设置默认密码为123456

//        employee.setCreateTime(LocalDateTime.now());//创建时间
//        employee.setUpdateTime(LocalDateTime.now());//更新时间


        //long id = (Long)request.getSession().getAttribute("employee");

//        employee.setCreateUser(1l);//创建人
//        employee.setUpdateUser(1l);//更新人

        log.info("新增员工信息如下：{}",employee.toString());//输出员工信息日志

        employeeService.save(employee);

        return R.success("员工创建成功");
    }


    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,@RequestParam("search") String name){
        log.info("page = {}, pageSize = {}, name = {}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

        //添加过滤条件 如果有用户则执行模糊查询
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //添加排序条件
        queryWrapper.orderByAsc(Employee::getId);//按照更新时间倒序排序 此处按照ID升序进行排序

        //执行查询
        employeeService.page(pageInfo,queryWrapper);//查询的信息自动封装到pageInfo中

        //返回成功的结果
        return R.success(pageInfo);
    }


    /**
     * 更新员工状态信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());//输出员工的信息

        //修改状态
//        int status = employee.getStatus() == 1 ? 0 : 1;
//        String msg = employee.getStatus() == 1 ? "禁用成功" : "启用成功";//根据消息判断禁用还是启用
//        employee.setStatus(status);

        //修改修改人和修改时间
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(empId);
//        employee.setUpdateTime(LocalDateTime.now());
        //修改
        employeeService.updateById(employee);

        return R.success("用户信息修改成功");
    }

    /**
     * 删除员工
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id){
        log.info(String.valueOf(id));//打印员工信息

        //判断ID是否为空
        if(StringUtils.isNotEmpty(String.valueOf(id))){
            employeeService.removeById(id);
            return R.success("用户删除成功");
        }
        else{
            return R.error("用户删除失败");
        }
    }

    /**
     * 根据id查询用户信息
     * @param id
     * @return
     */
    @PostMapping("/getMsg/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }
}
