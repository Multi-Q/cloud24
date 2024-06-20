package com.redis6.jedis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * @author QRH
 * @date 2024/6/18 22:03
 * @description TODO
 */
public class JedisTest {

    private Jedis jedis ;

    @BeforeEach
    void setUp(){
        jedis=new Jedis("192.168.101.65",6379);
        jedis.auth("redis");
        jedis.select(0);
    }

    @Test
    public void testString(){
        String res = jedis.set("name", "QRH");
        System.out.println(res);
        System.out.println(jedis.get("name"));
    }

    @Test
    public void testHash(){
        Map<String, String> hash = new HashMap<>();
        hash.put("name","王五");
        hash.put("age","34");
        hash.put("sex","men");

        System.out.println(jedis.hset("user:3", hash));
        System.out.println(jedis.hgetAll("user:3"));
    }




//    @AfterEach
//    public void countDown(){
//        if (jedis!=null){
//            jedis.shutdown();
//        }
//    }

}
