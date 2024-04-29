package com.redis.demo;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * @author QRH
 * @date 2024/4/13 10:30
 * @description TODO
 */
public class LettuceDemo {

    public static void main(String[] args)
    {
            //使用构建器builder创建RedisClient实例
        RedisURI uri = RedisURI.builder()
                .redis("192.168.177.178")
                .withPort(6379)
                .withAuthentication("default", "redis")
                .build();
        //创建连接客户端
        RedisClient redisClient = RedisClient.create(uri);
        StatefulRedisConnection<String, String> connect = redisClient.connect();
        //创建操作的command

        RedisCommands<String, String> commands = connect.sync();
        //--------------------------------------

        System.out.println(commands.keys("*"));

        commands.set("age","18");
        System.out.println(commands.get("age"));



        //--------------------------------------

        //各种关闭释放资源
        connect.close();
        redisClient.shutdown();
        System.out.println("end");


    }
}
