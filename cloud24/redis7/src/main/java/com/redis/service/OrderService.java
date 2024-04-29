package com.redis.service;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author QRH
 * @date 2024/4/13 10:53
 * @description TODO
 */
@Service
public class OrderService {

    private final static String ORDER_KEY="ord";

    @Resource
    private  RedisTemplate redisTemplate;

    public void addOrder(){
        int keyId = ThreadLocalRandom.current().nextInt(1000) + 1;
        String serialNo = UUID.randomUUID().toString();


        String key = ORDER_KEY + keyId;
        String value = "京东订单" + serialNo;

        redisTemplate.opsForValue().set(key,value);
        System.out.println("key:"+key+",value:"+value);
    }

    public String getOrder(String keyId){

        return (String) redisTemplate.opsForValue().get(keyId);
    }

}
