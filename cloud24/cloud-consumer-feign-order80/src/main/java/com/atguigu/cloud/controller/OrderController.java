package com.atguigu.cloud.controller;

import com.atguigu.cloud.apis.PayFeignApi;
import com.atguigu.cloud.entities.PayDTO;
import com.atguigu.cloud.resp.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * @author QRH
 * @date 2024/3/15 13:22
 * @description TODO
 */
@RestController
public class OrderController {
    @Resource
    private PayFeignApi payFeignApi;

    @PostMapping(value = "/feign/pay/add")
    public ResultData<String> addOrder(@RequestBody PayDTO payDTO) {
        System.out.println("第一步，模拟本地addOrder新增订单成功，第一步在开启addPay支付微服务远程调用");
        return payFeignApi.addPay(payDTO);
    }

    @GetMapping(value = "/feign/pay/get/{id}")
    public ResultData getPayInfo(@PathVariable("id")Integer id){
        System.out.println("支付微服务远程调用，按照id查询订单支付流水信息");
        return payFeignApi.getPayInfo(id);
    }

    @GetMapping(value="/feign/pay/info")
    public String getMylb(){
        return payFeignApi.mylb();
    }

}
