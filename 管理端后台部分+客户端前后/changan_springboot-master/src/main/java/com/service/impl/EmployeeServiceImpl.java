package com.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entity.Employee;
import com.mapper.EmployeeMapper;
import com.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * 数据库接口方法的实现类
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
