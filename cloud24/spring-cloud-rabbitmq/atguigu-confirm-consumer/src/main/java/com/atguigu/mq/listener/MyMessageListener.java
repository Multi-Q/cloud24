package com.atguigu.mq.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author QRH
 * @date 2024/5/21 14:37
 * @description TODO
 */
@Component
@Slf4j
public class MyMessageListener {

    @RabbitListener(queues = {"queue.order"})
    public void processMessage(String dataString, Message message, Channel channel) {
        //获取当前消息的deliverTag
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("消费端 消息内容： " + dataString);
            //操作成功，返回ack
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            //获取消息是否是重复投递的
            Boolean redelivered = message.getMessageProperties().getRedelivered();

            //操作失败，返回nack信息
            //requeue 控制消息是否重新放回队列
            try {
                if (redelivered) {
                    //重复投递，说明已经重试过一次了，不需要重新投递
                    channel.basicNack(deliveryTag, false, false);
                } else {
                    channel.basicNack(deliveryTag, false, true);
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } finally {
        }
    }
}
