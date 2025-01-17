package com.atguigu.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServiceProductMain {
    public static void main(String[] args) {
        SpringApplication.run(ServiceProductMain.class, args);
    }
}