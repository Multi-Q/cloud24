package com.atguigu.cloud.apis;

import com.atguigu.cloud.entities.PayDTO;
import com.atguigu.cloud.resp.ResultData;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author QRH
 * @date 2024/3/21 14:31
 * @description TODO
 */
//@FeignClient(value = "cloud-payment-service")
@FeignClient(value = "cloud-gateway",path="/pay")
public interface PayFeignApi {

    @PostMapping(value = "/add/")
    public ResultData<String> addPay(@RequestBody PayDTO payDTO);

    @GetMapping(value = "/get/{id}")
    public ResultData getPayInfo(@PathVariable("id") Integer id);

    @DeleteMapping("/del/{id}")
    public ResultData<Integer> deletePay(@PathVariable("id") Integer id);

    @PutMapping(value = "/update")
    public ResultData<String> updatePay(@RequestBody PayDTO payDTO);

    @GetMapping(value = "/get/{id}")
    public ResultData<PayDTO> getById(@PathVariable("id") Integer id);

    @GetMapping(value = "/get")
    public ResultData<List<PayDTO>> getAll();

    @GetMapping(value = "/get/info")
    public String mylb();

    /**
     * 测试熔断 Resilience4j CircuitBreak断路器
     * @param id
     * @return 提示信息
     */
    @GetMapping(value = "/circuit/{id}")
    public String myCircuit(@PathVariable("id") Integer id);

    /**
     * 测试熔断 Resilience4j BulkHead
     * @param id
     * @return 提示信息
     */
    @GetMapping(value = "/bulkhead/{id}")
    public String myBulkHead(@PathVariable("id") Integer id);

    @GetMapping(value = "/ratelimit/{id}")
    public String myRateLimiter(@PathVariable("id") Integer id);

    /**
     * Micrometer链路追踪
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/micrometer/{id}")
    public String myMicrometer(@PathVariable("id") Integer id);


    @GetMapping(value = "/gateway/get/{id}")
    public ResultData getGateWayById(@PathVariable("id") Integer id);

    @GetMapping(value = "/gateway/get/info")
    public ResultData getGateWayInfo();



}
