package com.atguigu.cloud.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.atguigu.cloud.entities.Pay;
import com.atguigu.cloud.resp.ResultData;
import com.atguigu.cloud.service.PayService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;

/**
 * @author QRH
 * @date 2024/4/1 22:16
 * @description TODO
 */
@RestController
public class PayGateWayController {

    @Resource
    private PayService payService;

    @GetMapping(value = "/pay/gateway/get/{id}")
    public ResultData<Pay> getGateWayById(@PathVariable("id") Integer id){
        return ResultData.success(payService.getById(id));
    }

    @GetMapping(value = "/pay/gateway/get/info")
    public ResultData<String> getGateWayInfo(){
        return ResultData.success("gateway info test: "+ IdUtil.simpleUUID());
    }

    @GetMapping(value = "/pay/gateway/filter")
    public ResultData<String> getGateWayFilter(HttpServletRequest request){
        String result="";
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()){
            String headerName = headers.nextElement();
            String headerValue = request.getHeader(headerName);
            System.out.println("请求头名： "+headerName+"\t\t\t请求头值： "+headerValue);
            if (headerName.equalsIgnoreCase("X-Request-atguigu1")||headerName.equalsIgnoreCase("X-Request-atguigu2")){
                result=result+headerName+"\t"+headerValue+" ";
            }

        }
        System.out.println("=============================");
        String customerId = request.getParameter("customerId");
        System.out.println("request parameter customId: "+customerId);

        String customerName = request.getParameter("customerName");
        System.out.println("request parameter customerName: "+customerName);
        System.out.println("=============================");
        return ResultData.success("getGateWayFilter 过滤器 test： "+result+" \t"+ DateUtil.now());
    }

}
