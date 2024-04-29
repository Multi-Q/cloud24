package com.redis.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author QRH
 * @date 2024/3/14 15:52
 * @description 激活启动swagger3 api文档
 */
@Configuration
public class Swagger3Config {
    @Bean
    public GroupedOpenApi payApi(){
        return  GroupedOpenApi.builder().group("订单微服务模块").pathsToMatch("/**").build();
    }

    @Bean
    public OpenAPI docsOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("redis7")
                        .description("通用设计rest")
                        .version("v1.0"))
                .externalDocs(new ExternalDocumentation()
                        .description("www.atguigu.com")
                        .url("http://www.baidu.com/")
                );
    }
}
