package com.example.publisher;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author QRH
 * @date 2024/5/11 17:13
 * @description TODO
 */
@SpringBootTest
public class PublisherTest {
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    public void send(){
        rabbitTemplate.convertAndSend("simple.queue","Hello RabbitMQ!");
    }

    @Test
    public void workQueue(){
        for (int i = 0; i < 50; i++) {
            rabbitTemplate.convertAndSend("work.queue","Hello RabbitMQ!+++Work Queue"+i);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void fanoutQueue(){
        for (int i = 0; i < 50; i++) {
            rabbitTemplate.convertAndSend("hmall.fanout",null,"Hello RabbitMQ!+++fanout Queue"+i);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void directQueue(){
        for (int i = 0; i < 50; i++) {
            rabbitTemplate.convertAndSend("hmall.exchange","red","蓝色警报 direct Queue"+i);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void topicQueue(){
        for (int i = 0; i < 50; i++) {
            rabbitTemplate.convertAndSend("hmall.topic","china.news","蓝色警报，哥斯拉把日本给灭了 topic Queue"+i);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testSendObject(){
        Map<String, Object> map = new HashMap<>();
        map .put("name","jack");
        map.put("age",18);

        this.rabbitTemplate.convertAndSend("object.queue",map);

    }



}
