package com.atguigu.cloud.gateway;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author QRH
 * @date 2024/4/2 23:29
 * @description 自定义断言类
 * 按照会员等级进行判断（钻、金、银）三个等级
 */
@Component //必须加这个注解
public class MyRoutePredicateFactory extends AbstractRoutePredicateFactory<MyRoutePredicateFactory.Config> {

    public static final String USER_TYPE_KEY = "userType";

    public MyRoutePredicateFactory() {
        super(MyRoutePredicateFactory.Config.class);
    }

    //开启Shortcut配置，因为路由断言有两种配置方式：shortcut和fully expend
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("userType");
    }

    @Override
    public Predicate<ServerWebExchange> apply(MyRoutePredicateFactory.Config config) {
        return new Predicate<ServerWebExchange>() {
            public boolean test(ServerWebExchange serverWebExchange) {
                //这里是使用的是Query Route Predicate的写法，
                String userType = serverWebExchange.getRequest().getQueryParams().getFirst("userType");
                if(userType==null) return false;
                if (userType.equalsIgnoreCase(config.getUserType())) return true;

                return false;
            }

            public Object getConfig() {
                return config;
            }

            public String toString() {
                return String.format("MyRoutePredicateFactory: %s", config.getUserType());
            }

        };
    }




    @Validated
    public static class Config {
        @Setter
        @Getter
        @NotEmpty
        private String userType; //钻、金、银等用户等级
}


}
