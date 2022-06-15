package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.R;
import com.dto.OrderDto;
import com.entity.OrderDetail;
import com.entity.Orders;
import com.service.OrderDetailService;
import com.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Resource
    private OrdersService service;

    @Resource
    private OrderDetailService detailService;

    /**
     * 用户查询属于自己的订单数据
     * @param request
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> getOrders(HttpServletRequest request,int page,int pageSize){
        //获取用户的id
        Long id = (Long) request.getSession().getAttribute("userId");

        //分页参数
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrderDto> dtoPage = new Page<>();

        //条件查询
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,id);
        queryWrapper.orderByDesc(Orders::getCheckoutTime);
        service.page(pageInfo,queryWrapper);

        //拷贝
        BeanUtils.copyProperties(page,dtoPage,"records");
        List<Orders> orders= pageInfo.getRecords();
        List<OrderDto> dtoRecords = orders.stream().map((item) -> {
            OrderDto orderDto = new OrderDto();//每一个dto对象
            BeanUtils.copyProperties(item,orderDto);//先拷贝属性

            //找出对应的数据
            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderDetail::getOrderId,item.getId());
            List<OrderDetail> list = detailService.list(wrapper);
            orderDto.setOrderDetails(list);

            return orderDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(dtoRecords);

        return R.success(dtoPage);
    }

    /**
     * 管理员查询所有的订单数据
     * @param page
     * @param pageSize
     * @param search
     * @return
     */
    @GetMapping("/page")
    public R<Page> getOrder(int page, int pageSize, String search, LocalDateTime checkoutTime){
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        List<Integer> list = new ArrayList<>();
        list.add(2);list.add(3);list.add(4);

        //搜索
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(checkoutTime != null,Orders::getCheckoutTime,checkoutTime);
        queryWrapper.in(Orders::getStatus,list);
        queryWrapper.like(StringUtils.isNotEmpty(search),Orders::getPhone,search);
        service.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 查询对应订单的详细菜品信息
     * @param number
     * @return
     */
    @GetMapping("/detail")
    public R<List<OrderDetail>> getOrderDetail(Long number){
        List<OrderDetail> detailList = new ArrayList<>();

        //查询订单号对应的id
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getNumber,number);
        Long orderId =  service.getOne(queryWrapper).getId();

        //根据订单id查询订单的详细信息
        LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDetail::getOrderId,orderId);
        for(OrderDetail detail : detailService.list(wrapper)){
            detailList.add(detail);
        }

        //返回结果
        return R.success(detailList);
    }

    @PutMapping("/status/{stu}")
    public R<String> updateStatus(@RequestBody List<Long> ids,@PathVariable("stu") int status){
        List<Integer> list = new ArrayList<>();
        list.add(2);list.add(3);

        for(Long id : ids){
            //根据id改变对应的状态
            LambdaUpdateWrapper<Orders> wrapper = new LambdaUpdateWrapper<>();
            wrapper.in(Orders::getStatus,list);//4不能被改变
            wrapper.eq(Orders::getId,id);
            wrapper.set(Orders::getStatus,status);
            service.update(wrapper);
        }
        return R.success("修改成功");
    }

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders, HttpServletRequest request){
        Long id = (Long) request.getSession().getAttribute("userId");
        log.info("订单数据：{}",orders);
        service.submit(orders,id);
        return R.success("下单成功");
    }

}
