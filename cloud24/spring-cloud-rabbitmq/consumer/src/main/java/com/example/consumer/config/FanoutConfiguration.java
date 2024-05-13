package com.example.consumer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author QRH
 * @date 2024/5/12 19:43
 * @description TODO
 *
 * @note 注意这里有个坑：
 * 启动项目时，会报存在多个bean，其原因是这个类里声明了多个bean，而bindingQueue3（）又调用了fanoutBinding3（）和
 * fanoutExchange（），因为spring容器加载bean不会从上到下执行，当执行到bindingQueue3（）时，
 * fanoutExchange（）和fanoutQueue3（）还未初始化，所以导致了错误
 */
@Configuration
public class FanoutConfiguration {

    //声明交换机，广播模式
    @Bean
    @Primary
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("hmall.fanout22");
    }

    //声明队列，命名为hmall.queue3
    @Bean
    @Primary
    public Queue fanoutQueue3() {
        return new Queue("hmall.queue3");
    }

    //绑定关系，将队列绑定到交换机中
    @Bean
    public Binding bindingQueue3(Queue fanoutQueue3, FanoutExchange fanoutExchange){
        return BindingBuilder.bind(fanoutQueue3).to(fanoutExchange);
    }

    //声明队列，命名为hmall.queue4
    @Bean
    public Queue fanoutQueue4() {
        return new Queue("hmall.queue4");
    }

    //绑定关系，将队列绑定到交换机中
    @Bean
    public Binding fanoutBinding4( ) {
        return BindingBuilder.bind(fanoutQueue4()).to(fanoutExchange());
    }
}
