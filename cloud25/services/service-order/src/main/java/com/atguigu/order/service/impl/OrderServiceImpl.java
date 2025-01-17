package com.atguigu.order.service.impl;

import com.atguigu.model.Order;
import com.atguigu.model.Product;
import com.atguigu.order.service.OrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Override
    public Order createOrder(Long productId, Long userId) {
        Order order = new Order();
        order.setId(productId);
        order.setNickName("张三三");
        order.setTotalAmount(new BigDecimal("120"));
        order.setUserId(userId);
        order.setAddress("北京路");
        order.setProductList(null);
        return order;
    }

    @Override
    public Order createOrderByFeign(Long userId, List<Product> productList) {
        Order order = new Order();
        order.setId(1234567L);
        order.setUserId(userId);
        order.setAddress("北京路");
        order.setNickName("张三三");
        order.setProductList(productList);
        BigDecimal sum = new BigDecimal("0");
        for (Product p : productList) {
            sum = sum.add(p.getPrice().multiply(new BigDecimal(p.getNum().toString())));
        }
        order.setTotalAmount(sum);

        return order;
    }
}
