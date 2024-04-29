package com.redis.controller;

import com.redis.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author QRH
 * @date 2024/4/13 10:59
 * @description TODO
 */
@RestController
@Tag(name = "订单微服务模块", description = "订单crud")
public class OrderController {

    @Resource
    private OrderService orderService;;

    @Operation(summary = "新增", description = "新增订单")
    @PostMapping("/order/add")
    public void addOrder(){
        orderService.addOrder();
    }

    @Operation(summary = "获取", description = "根据id获取订单")
    @GetMapping("/order/get/{id}")
    public String getOrder(@PathVariable("id") String id){
        return orderService.getOrder(id);
    }

}
