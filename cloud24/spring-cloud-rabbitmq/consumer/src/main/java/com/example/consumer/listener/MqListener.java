package com.example.consumer.listener;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @author QRH
 * @date 2024/5/11 17:28
 * @description TODO
 */
@Component
public class MqListener {

    @RabbitListener(queues = "simple.queue")
    public void onMessage(String message) {
        System.out.println("消费者接收到消息：---" + message);
    }

    @RabbitListener(queues = "work.queue")
    public void workQueueMsg1(String message) {
        System.out.println("【消费者1】接收到消息：---" + message);
    }

    @RabbitListener(queues = "work.queue")
    public void workQueueMsg2(String message) {
        System.out.println("【消费者2】接收到消息：---" + message);
    }

    @RabbitListener(queues = "fanout.queue1")
    public void fanoutQueue1(String message) {
        System.out.println("【消费者1】接收到消息：fanout1---" + message);
    }

    @RabbitListener(queues = "fanout.queue2")
    public void fanoutQueue2(String message) {
        System.out.println("【消费者2】接收到消息：fanout2---" + message);
    }

    @RabbitListener(
            bindings = {
                    @QueueBinding(
                    value = @Queue(value = "direct.queue11"),
                    exchange = @Exchange(value = "hmall.exchange"),
                    key = {"blue", "red"}
            )}

    )
    public void directQueue1(String message) {
        System.out.println("【消费者1】接收到消息：direct.queue1---" + message);
    }

    @RabbitListener(
            bindings = {
                    @QueueBinding(
                           value = @Queue(value = "direct.queue22"),
                            exchange = @Exchange(value = "hmall.exchange"),
                            key = {"yellow","red"}
                    )
            }
    )
    public void directQueue2(String message) {
        System.out.println("【消费者2】接收到消息：direct.queue2---" + message);
    }

    @RabbitListener(queues = "topic.queue1")
    public void topicQueue1(String message) {
        System.out.println("【消费者1】接收到消息：topic.queue1---" + message);
    }

    @RabbitListener(queues = "topic.queue2")
    public void topicQueue2(String message) {
        System.out.println("【消费者2】接收到消息：topic.queue2---" + message);
    }


}
