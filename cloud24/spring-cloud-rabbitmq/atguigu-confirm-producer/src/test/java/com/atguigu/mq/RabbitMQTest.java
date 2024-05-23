package com.atguigu.mq;

import com.atuigu.mq.Main8087;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author QRH
 * @date 2024/5/21 14:17
 * @description TODO
 */
@SpringBootTest(classes = {Main8087.class})
public class RabbitMQTest {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSendMessage() {
        rabbitTemplate.convertAndSend("exchange.direct.order","order", "确认机制MessageTest");
    }
}
