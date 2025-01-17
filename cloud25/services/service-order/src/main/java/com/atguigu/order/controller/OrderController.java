package com.atguigu.order.controller;

import com.atguigu.model.Order;
import com.atguigu.model.Product;
import com.atguigu.order.property.ServiceOrderProperty;
import com.atguigu.order.service.OrderService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class OrderController {

    @Resource
    private OrderService orderService;

    @GetMapping("order/{productId}/{userId}")
    public Order createOrder(@PathVariable Long productId, @PathVariable Long userId) {
        return orderService.createOrder(productId, userId);
    }

    @PostMapping("/order/create/feign/{userId}")
    public Order createOrderByFeign(@PathVariable("userId") Long userId,@RequestBody List<Product> productList){
        return orderService.createOrderByFeign(userId,productList);
    }

     @Resource
     private ServiceOrderProperty serviceOrderProperty;

    @GetMapping("/order/nacos/prop")
    public String getNacosProp(){
        String prop = serviceOrderProperty.getTimeout() + "\t" + serviceOrderProperty.getDay();
        return prop;
    }
}
