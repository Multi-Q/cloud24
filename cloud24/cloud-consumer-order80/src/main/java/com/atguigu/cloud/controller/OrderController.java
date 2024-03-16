package com.atguigu.cloud.controller;

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
//@RequestMapping(value = "/order")
public class OrderController {

    public static final  String PaymentSrv_URL="http://cloud-payment-service";

    @Resource
    private RestTemplate restTemplate;

    @GetMapping(value = "/consumer/pay/add")
    public ResultData addOrder(PayDTO payDTO){
        return restTemplate.postForObject(PaymentSrv_URL+"/pay/add",payDTO,ResultData.class);
    }

    @GetMapping(value = "/consumer/pay/get/{id}")
    public ResultData getPayInfo(@PathVariable("id") Integer id){
        return restTemplate.getForObject(PaymentSrv_URL+"/pay/get/"+id,ResultData.class,id);
    }

    @GetMapping(value = "/consumer/pay/del/{id}")
    public void deletePay(@PathVariable("id") Integer id){
          restTemplate.delete(PaymentSrv_URL+"/pay/del/"+id,id);
    }

    @GetMapping(value="/consumer/pay/get")
    public ResultData getAllPay(){
        return  restTemplate.getForObject(PaymentSrv_URL+"/pay/get",ResultData.class);
    }
}
