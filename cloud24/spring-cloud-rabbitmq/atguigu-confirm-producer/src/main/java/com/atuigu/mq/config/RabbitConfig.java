package com.atuigu.mq.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

/**
 * @author QRH
 * @date 2024/5/21 14:07
 * @description TODO
 */
@Configuration
@Slf4j
public class RabbitConfig implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void initRabbitTemplate() {
        //设置确认回调
        rabbitTemplate.setConfirmCallback(this);
        //设置失败回调
        rabbitTemplate.setReturnsCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        //消息发送到交换机成功或失败时调用这个方法
        log.info("confirm() 回调函数打印correlationData： "+correlationData);
        log.info("confirm() 回调函数打印ack： "+ack);
        log.info("confirm() 回调函数打印cause： "+cause);
    }

    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        //发送到队列失败才调用的方法
        log.info("returnedMessage() 回调函数打印消息主体： "+new String(returnedMessage.getMessage().getBody()));
        log.info("returnedMessage() 回调函数打印应答码： "+returnedMessage.getReplyCode());
        log.info("returnedMessage() 回调函数打印描述： "+returnedMessage.getReplyText());
        log.info("returnedMessage() 回调函数打印消息使用的交换机： "+returnedMessage.getExchange());
        log.info("returnedMessage() 回调函数打印消息使用的路由键： "+returnedMessage.getRoutingKey());
    }
}
