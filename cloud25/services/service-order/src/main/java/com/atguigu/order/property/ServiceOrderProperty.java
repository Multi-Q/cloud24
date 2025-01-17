package com.atguigu.order.property;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "order")
@Data
//@RefreshScope
public class ServiceOrderProperty {
//    @Value("${order.timeout}")
    private String timeout;
//    @Value("${order.day}")
    private String day;

    /**
     * 注意的是@ConfigurationProperties(prefix = "order")与@Value是冲突的
     *  二选一
     *  @ConfigurationProperties(prefix = "order")实现无感刷新
     *  @RefreshScope+@Value实现无感刷新
     */
}
