package com.atguigu.order.service;


import com.atguigu.model.Order;
import com.atguigu.model.Product;

import java.util.List;

public interface OrderService {
    public Order createOrder(Long productId, Long userId);

    Order createOrderByFeign(Long userId, List<Product> productList);

}
