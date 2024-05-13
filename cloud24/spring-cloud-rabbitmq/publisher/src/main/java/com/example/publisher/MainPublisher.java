package com.example.publisher;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author QRH
 * @date 2024/5/11 17:12
 * @description TODO
 */
@SpringBootApplication
public class MainPublisher {
    public static void main(String[] args) {
        SpringApplication.run(MainPublisher.class, args);
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
