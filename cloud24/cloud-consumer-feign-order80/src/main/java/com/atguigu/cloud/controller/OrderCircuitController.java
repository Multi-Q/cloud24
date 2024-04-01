package com.atguigu.cloud.controller;

import com.atguigu.cloud.apis.PayFeignApi;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author QRH
 * @date 2024/3/27 21:17
 * @description TODO
 */
@RestController
public class OrderCircuitController {
    @Resource
    private PayFeignApi payFeignApi;

    @GetMapping(value = "/feign/pay/circuit/{id}")
    @CircuitBreaker(name="cloud-payment-service",fallbackMethod = "myCircuitFallback")
    public String myCircuitBreaker(@PathVariable("id") Integer id){
        return  payFeignApi.myCircuit(id);
    }

    /**
     * 舱壁
     * @param id
     * @return
     */
    @GetMapping(value = "/feign/pay/bulkhead/{id}")
    @Bulkhead(name="cloud-payment-service",fallbackMethod = "myBulkheadFallback",type = Bulkhead.Type.SEMAPHORE)
    public String myBulkHead(@PathVariable("id") Integer id){
        return  payFeignApi.myBulkHead(id);
    }

    /**
     * 舱壁 固定线程池
     * @param id
     * @return
     */
    @GetMapping(value = "/feign/pay/bulkheadpool/{id}")
    @Bulkhead(name="cloud-payment-service",fallbackMethod = "myBulkheadPoolFallback",type = Bulkhead.Type.THREADPOOL)
    public CompletableFuture<String> myBulkHeadPool(@PathVariable("id") Integer id){
        System.out.println(Thread.currentThread().getName()+"\t"+"---开始进入");
        try {
            TimeUnit.SECONDS.sleep(3);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName()+"\t"+"---准备离开");
        return  CompletableFuture.supplyAsync(()->payFeignApi.myBulkHead(id));
    }

    /**
     * 限流器
     * @param id
     * @return
     */
    @GetMapping(value = "/feign/pay/ratelimiter/{id}")
    @RateLimiter(name="cloud-payment-service",fallbackMethod = "myRateLimiterFallback")
    public String myRateLimiter(@PathVariable("id") Integer id){
        return  payFeignApi.myRateLimiter(id);
    }

    public String myRateLimiterFallback(Integer id,Throwable t){
        return "myRateLimiterFallback，你被限流了，禁止访问----~~~~";
    }


    public CompletableFuture<String> myBulkheadPoolFallback(Integer id,Throwable t){
        return  CompletableFuture.supplyAsync(()->"myBulkheadPoolFallback ，舱壁超出最大数量限制， 系统繁忙，请稍后重试----~~~~");
    }

    public String myBulkheadFallback(Integer id,Throwable t){
        return "myBulkheadFallback ，舱壁超出最大数量限制， 系统繁忙，请稍后重试----~~~~";
    }

    //myCircuitFallback就是服务熔断降级后的兜底处理方法
    public String myCircuitFallback(Integer id,Throwable t){
        return "myCircuitFallback，系统繁忙，请稍后重试----~~~~";
    }
}
