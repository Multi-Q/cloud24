package com.atguigu.cloud.controller;

import com.atguigu.cloud.service.FlowLimitService;
import jakarta.annotation.Resource;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author QRH
 * @date 2024/4/4 13:38
 * @description TODO
 */
@RestController
public class FlowLimitController {

    @Resource
    private FlowLimitService flowLimitService;

    @GetMapping(value = "/testA")
    public String testA(){
        return "----testA";
    }

    @GetMapping(value = "/testB")
    public String testB(){
        return "----testB";
    }


    @GetMapping(value = "/testC")
    public String testC(){
        flowLimitService.common();
        return "----testD";
    }
    @GetMapping(value = "/testD")
    public String testD(){
        flowLimitService.common();
        return "----testD";
    }




}
