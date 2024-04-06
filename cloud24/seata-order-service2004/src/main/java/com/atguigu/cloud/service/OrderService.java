package com.atguigu.cloud.service;

import com.atguigu.cloud.entities.Order;

/**
 * @author QRH
 * @date 2024/4/6 11:36
 * @description TODO
 */
public interface OrderService {
    /**
     * 创建订单
     */
    void create(Order order);
}
