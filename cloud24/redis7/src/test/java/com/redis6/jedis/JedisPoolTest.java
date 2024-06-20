package com.redis6.jedis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * @author QRH
 * @date 2024/6/18 22:23
 * @description TODO
 */
public class JedisPoolTest {
    private static final Jedis jedis;

    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(8);
        jedisPoolConfig.setMaxIdle(8);
        jedisPoolConfig.setMinIdle(0);
        jedisPoolConfig.setMaxWait(Duration.ofMillis(10000));
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, "192.168.101.65", 6379, 1000, "redis");
        jedis = jedisPool.getResource();
    }

    @Test
    public void testString(){
        System.out.println(jedis.get("name"));
    }

    @Test
    public void testHash(){
        System.out.println(jedis.hgetAll("user:3"));
    }


    @AfterEach
    public void close(){
        if(jedis!=null){
            jedis.close();
        }
    }

}
