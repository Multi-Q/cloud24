package com.redis6.springdata;

import com.redis.MainRedis;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author QRH
 * @date 2024/6/18 22:48
 * @description TODO
 */
@SpringBootTest(classes = MainRedis.class)
public class SpringDataRedisTest {

    @Resource
    private RedisTemplate redisTemplate;


    @Test
    public void testString() {
      redisTemplate.opsForValue().set("time","新时代");
        System.out.println(redisTemplate.opsForValue().get("time"));
    }

}
