package com.atguigu.cloud.resp;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author QRH
 * @date 2024/3/15 11:32
 * @description 统一信息返回值枚举类
 */
@Getter
public enum ReturnCodeEnum {
    RC999("999", "操作xxx失败"),
    RC200("200", "success"),
    RC201("201", "服务器开启降级保护，请稍后再试！"),
    RC202("202", "热点参数限流，请稍后再试！"),
    RC203("203", "系统规则不满住要去，请稍后再试！"),
    RC204("204", "授权规则不通过，请稍后再试！"),
    RC403("403", "无法访问全限，请联系管理员授予权限"),
    RC401("401", "匿名用户访问无权权限资源时的异常"),
    RC404("404", "404页面找不到的异常"),
    RC500("500", "系统异常，请稍后重试"),
    RC375("375", "数字运算异常，请稍后再试"),

    INVALID_TOKEN("2001", "访问令牌不合法"),
    ACCESS_DENIED("2003", "没有权限访问该资源"),
    CLIENT_AUTHENTICATION_FAILED("1001", "客户端认证失败"),
    USERNAME_OR_PASSWORD_ERROR("1002", "用户名或密码错误"),
    BUSINESS_ERROR("1004", "业务逻辑异常"),
    UNSUPPORTED_GRANT_TYPE("1003", "不支持的认证模式");

    private final String code;
    private final String message;

    ReturnCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    //    遍历枚举
    public static ReturnCodeEnum getReturnCodeEnum(String code) {
        for (ReturnCodeEnum e : ReturnCodeEnum.values()) {
            if (e.getCode().equalsIgnoreCase(code)) {
                return e;
            }
        }
        return null;
    }

    public static ReturnCodeEnum getReturnCodeEnum2(String code) {
        return Arrays.stream(ReturnCodeEnum.values())
                .filter(
                        x -> x.getCode()
                                .equalsIgnoreCase(code)
                )
                .findFirst()
                .orElse(null);
    }
}
