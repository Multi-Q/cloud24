package com.atguigu.cloud.config;

import feign.Logger;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author QRH
 * @date 2024/3/23 13:44
 * @description Feign 配置类
 */
@Configuration
public class FeignConfig {

    @Bean
    public Retryer myRetryer() {
        return Retryer.NEVER_RETRY;
        //最大请求次数为3(1 default +2)，出时间间隔时间为100ms，重试最大间隔时间为1s
//        return new Retryer.Default(100,1,3);
    }

    @Bean
   public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

}
