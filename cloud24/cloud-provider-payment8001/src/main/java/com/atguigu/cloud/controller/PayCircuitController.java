package com.atguigu.cloud.controller;

import cn.hutool.core.util.IdUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author QRH
 * @date 2024/3/27 20:44
 * @description TODO
 */
@RestController
public class PayCircuitController {

    /**
     * Resilience4j circuitBreaker的例子
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/pay/circuit/{id}")
    public String myCircuit(@PathVariable("id") Integer id) {
        if (id == -4) throw new RuntimeException("---circuit id不能为负数");
        if (id == 9999) try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Hello ,Circuit inputId: " + id + " \t" + IdUtil.simpleUUID();
    }
}
