package com.atguigu.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author QRH
 * @date 2024/4/29 14:13
 * @description TODO
 */
@Configuration
//@EnableWebSecurity //开启spring security的自定义配置(springboot项目可以省略这个注解，非springboot项目必须加）
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http.authorizeHttpRequests(
                authorize->authorize.
                        //对所有请求开启授权保护
                        anyRequest()
                        //已认证的请求会被自动授权
                        .authenticated()
        )
                //使用表单授权方式
                .formLogin(Customizer.withDefaults())
        //使用基本授权方式
        //.httpBasic(Customizer.withDefaults())
                .csrf(csrf->csrf.disable())
        .build();
    }


//    @Bean
//    public UserDetailsService  userDetailsService(){
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        manager.createUser(
//              User.withDefaultPasswordEncoder()
//                      .username("user")
//                      .password("123456")
//                      .roles("USER")
//                      .build()
//      );
//        return manager;
//    }


//    @Bean
//    public UserDetailsService userDetailsService(){
//        return new DBUserDetailManager();
//    }

}
