package com.atguigu.cloud.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author QRH
 * @date 2024/4/5 11:31
 * @description TODO
 */
@RestController
public class RateLimiterController {

    @GetMapping(value = "/testHotKey")
    @SentinelResource(value = "testHotKey",blockHandler = "dealHandlerTestHotKey")
    public String testHotKey(@RequestParam(value = "p1",required = false)String p1,
                             @RequestParam(value = "p2",required = false)String p2){
        return "-------testHotKey";
    }

    public String dealHandlerTestHotKey(String p1, String p2, BlockException e){
        return "------------dealHandlerTestHotKey 点击太快，限流了";
    }
}
