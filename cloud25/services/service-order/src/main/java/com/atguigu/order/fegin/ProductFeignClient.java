package com.atguigu.order.fegin;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "service-product") //微服务名称
public class ProductFeignClient {

}
