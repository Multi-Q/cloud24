package com.atguigu.cloud.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.atguigu.cloud.entities.PayDTO;
import com.atguigu.cloud.resp.ResultData;
import com.atguigu.cloud.resp.ReturnCodeEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author QRH
 * @date 2024/4/4 0:10
 * @description TODO
 */
@RestController
public class PayAlibabaController {

    @Value("${server.port}")
    private String serverPort;

    @GetMapping(value = "/pay/nacos/{id}")
    public String getPayInfo(@PathVariable("id") Integer id) {
        return "nacos registry, serverPort: " + serverPort + "\t id" + id;
    }


    @GetMapping(value = "/pay/nacos/get/{orderNo}")
    @SentinelResource(value = "getPayByOrder",blockHandler = "handlerBlockHandler")
    public ResultData getPayByOrder(@PathVariable("orderNo")String orderNo) {
        PayDTO payDTO = new PayDTO();
        payDTO.setId(1024);
        payDTO.setOrderNo(orderNo);
        payDTO.setAmount(BigDecimal.valueOf(9.91));
        payDTO.setPayNo("pay: "+ IdUtil.fastUUID());
        payDTO.setUserId(1);
        return ResultData.success("查询返回值： "+payDTO);
    }
    public ResultData handlerBlockHandler(@PathVariable("orderNo")String orderNo,Throwable e) {
        return ResultData.fail(ReturnCodeEnum.RC500.getCode(),   "getPayByOrder服务不可用，触发sentinel流控配置规则");
    }

}
