package com.atguigu.mq.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author QRH
 * @date 2024/5/21 11:54
 * @description TODO
 */
@Component
public class MyMessageListener {
    public static final String EXCHANGE_DIRECT="exchange.direct.order";
    public static final String ROUTING_KEY="order";
    public static final String QUEUE_NAME="queue.order";

    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            value = @Queue(value = QUEUE_NAME),
                            exchange = @Exchange(value = EXCHANGE_DIRECT),
                            key = {ROUTING_KEY}
                    )
            }
    )
    public void processMessage(String dataString, Message message, Channel channel){
        System.out.println(dataString);
        System.out.println(message.toString());
        System.out.println(channel.toString());
    }




}
