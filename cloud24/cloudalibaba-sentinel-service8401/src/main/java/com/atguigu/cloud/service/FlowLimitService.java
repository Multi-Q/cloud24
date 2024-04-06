package com.atguigu.cloud.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.stereotype.Service;

/**
 * @author QRH
 * @date 2024/4/4 16:59
 * @description TODO
 */
@Service
public class FlowLimitService {
    @SentinelResource(value = "common")
    public void common() {
        System.out.println("--------flowlIMITsERVICE COME IN");
    }
}
