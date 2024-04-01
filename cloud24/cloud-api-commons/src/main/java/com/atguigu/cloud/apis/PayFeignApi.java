package com.atguigu.cloud.apis;

import com.atguigu.cloud.entities.PayDTO;
import com.atguigu.cloud.resp.ResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author QRH
 * @date 2024/3/21 14:31
 * @description TODO
 */
//@FeignClient(value = "cloud-payment-service")
@FeignClient(value = "cloud-gateway")
public interface PayFeignApi {

    @PostMapping(value = "/pay/add/")
    public ResultData<String> addPay(@RequestBody PayDTO payDTO);

    @GetMapping(value = "/pay/get/{id}")
    public ResultData getPayInfo(@PathVariable("id") Integer id);

    @DeleteMapping("/pay/del/{id}")
    public ResultData<Integer> deletePay(@PathVariable("id") Integer id);

    @PutMapping(value = "/pay/update")
    public ResultData<String> updatePay(@RequestBody PayDTO payDTO);

    @GetMapping(value = "/pay/get/{id}")
    public ResultData<PayDTO> getById(@PathVariable("id") Integer id);

    @GetMapping(value = "/pay/get")
    public ResultData<List<PayDTO>> getAll();

    @GetMapping(value = "/pay/get/info")
    public String mylb();

    /**
     * 测试熔断 Resilience4j CircuitBreak断路器
     * @param id
     * @return 提示信息
     */
    @GetMapping(value = "/pay/circuit/{id}")
    public String myCircuit(@PathVariable("id") Integer id);

    /**
     * 测试熔断 Resilience4j BulkHead
     * @param id
     * @return 提示信息
     */
    @GetMapping(value = "/pay/bulkhead/{id}")
    public String myBulkHead(@PathVariable("id") Integer id);

    @GetMapping(value = "/pay/ratelimit/{id}")
    public String myRateLimiter(@PathVariable("id") Integer id);

    /**
     * Micrometer链路追踪
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/pay/micrometer/{id}")
    public String myMicrometer(@PathVariable("id") Integer id);


    @GetMapping(value = "/pay/gateway/get/{id}")
    public ResultData getGateWayById(@PathVariable("id") Integer id);

    @GetMapping(value = "/pay/gateway/get/info")
    public ResultData getGateWayInfo();
}
