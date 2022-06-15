package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.Orders;

public interface OrdersService extends IService<Orders> {
    void submit(Orders orders,Long id);
}
