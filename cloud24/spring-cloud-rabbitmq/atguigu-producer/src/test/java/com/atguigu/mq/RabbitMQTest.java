package com.atguigu.mq;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author QRH
 * @date 2024/5/21 13:53
 * @description TODO
 */
@SpringBootTest
public class RabbitMQTest {
    public static final String EXCHANGE_DIRECT="exchange.direct.order";
    public static final String ROUTING_KEY="order";
    public static final String QUEUE_NAME="queue.order";

    @Resource
    private RabbitTemplate rabbitTemplate;



    @Test
    public void testSendMessage() {
        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT, ROUTING_KEY, "hello,rabbitmq atguigu");
    }



}
