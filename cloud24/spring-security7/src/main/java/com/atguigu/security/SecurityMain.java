package com.atguigu.security;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author QRH
 * @date 2024/4/20 0:10
 * @description TODO
 */
@SpringBootApplication
@MapperScan("com.atguigu.security.mapper")
public class SecurityMain {
    public static void main(String[] args) {
        SpringApplication.run(SecurityMain.class,args);
    }
}
