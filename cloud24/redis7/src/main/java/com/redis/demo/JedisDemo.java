package com.redis.demo;

import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

/**
 * @author QRH
 * @date 2024/4/12 11:27
 * @description Jedis
 */
public class JedisDemo {
    public static void main(String[] args) {
        // 创建jedis对象
        Jedis jedis = new Jedis("192.168.177.178", 6379);

        jedis.auth("redis");

        //测试是否链接成功
        System.out.println(jedis.ping());

        Set<String> keys = jedis.keys("*");
        System.out.println(keys);

        jedis.set("name","qrh");
        System.out.println(jedis.get("name"));

        //list
        jedis.lpush("mylist","11","22","33");
        List<String> mylist = jedis.lrange("mylist", 0, -1);
        System.out.println(mylist);



    }
}
