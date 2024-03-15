# SpringCloud学习笔记

## 第二天

### 一、时间格式转化

时间格式转化有三种方式：

* 1、在`相应的类的属性`上使用`@JsonFormat`注解

```java
public class xxx {
    //    .....
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date billtime;

//    .....
}
```

* 2、如果是Springboot项目，可以在application.yml中指定

```yml
spring:
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8
```

### 二、统一返回接口

接口统一返回值：<br>
①思路
> 返回接口的类的标准定义格式：
>> * 1、code状态值：由后端统一定义各种返回结果的状态码
>> * 2、message描述：本次接口调用的结果描述
>> * 3、data数据：本次返回的数据
>
> 扩展:
>> * 1、接口调用时间之类---timestamp：接口调用时间

②步骤
> * 1、新建枚举类ReturnCodeEnum
>> http请求返回状态码：
>>
>> |分类 |区间 |分类描述|
>> |:-----:|:----:|:-------:|
>> |1***|100~199|信息，服务器收到请求，需要请求者继续执行操作|
>> |2***|200~299|成功，操作被成功接收并处理|
>> |3***|300~399|重定向，需要进一步的操作已完成请求|
>> |4***|400~499|客户端错误，请求包含语法错误或无法完成请求|
>> |5***|500~599|服务器错误，服务器在处理请求的过程中发生了错误|
>>
>> **ReturnCodeEnum.class**
>>```java
>> package com.atguigu.cloud.enums;
>> import lombok.Getter;
>>
>>/**
>>* @author QRH
>>* @date 2024/3/15 11:32
>>* @description 统一信息返回值枚举类
>> */
>> @Getter
>> public enum ReturnCodeEnum {
>>  RC999("999", "操作xxx失败"),
>>  RC200("200", "success"),
>>  RC201("201", "服务器开启降级保护，请稍后再试！"),
>>  RC202("202", "热点参数限流，请稍后再试！"),
>>  RC203("203", "系统规则不满住要去，请稍后再试！"),
>>  RC204("204", "授权规则不通过，请稍后再试！"),
>>  RC403("403", "无法访问全限，请联系管理员授予权限"),
>>  RC401("401", "匿名用户访问无权权限资源时的异常"),
>>  RC404("404", "404页面找不到的异常"),
>>  RC500("500", "系统异常，请稍后重试"),
>>  RC375("375", "数字运算异常，请稍后再试"),
>>
>>  INVALID_TOKEN("2001", "访问令牌不合法"),
>>  ACCESS_DENIED("2003", "没有权限访问该资源"),
>>  CLIENT_AUTHENTICATION_FAILED("1001", "客户端认证失败"),
>>  USERNAME_OR_PASSWORD_ERROR("1002", "用户名或密码错误"),
>>  BUSINESS_ERROR("1004", "业务逻辑异常"),
>>  UNSUPPORTED_GRANT_TYPE("1003", "不支持的认证模式");
>>
>>  private final String code;
>>  private final String message;
>>
>>  ReturnCodeEnum(String code, String message) {
>>   this.code = code;
>>   this.message = message;
>>  }
>>
>> //    遍历枚举
>> public static ReturnCodeEnum getReturnCodeEnum(String code){
>>   for (ReturnCodeEnum e :ReturnCodeEnum.values()){
>>     if (e.getCode().equalsIgnoreCase(code)){
>>       return e;
>>     }
>>    }
>>   return null;
>> }
>>
>>}
>>
>>```
> * 2、定义统一返回对象ResultData
>> **ResultData.class**
>> ```java
>>  package com.atguigu.cloud.resp;
>>
>> import lombok.Data;
>> import lombok.experimental.Accessors;
>>
>> /**
>> * @author QRH
>> * @date 2024/3/15 11:54
>> * @description 统一返回数据对象
>>   */
>>   @Data
>>   @Accessors(chain = true)
>>   public class ResultData<T> {
>>    private String code;/** 结果状态 ,具体状态码参见枚举类ReturnCodeEnum.java*/
>>    private String message;
>>    private T data;
>>    private long timestamp ;
>>
>>    public ResultData() {
>>     this.timestamp=System.currentTimeMillis();
>>    }
>>
>>   public static <T> ResultData<T> success(T data){
 >>    ResultData<T> resultData = new ResultData<>();
 >>    resultData.setCode(ReturnCodeEnum.RC200.getCode());
>>     resultData.setMessage(ReturnCodeEnum.RC200.getMessage());
>>     resultData.setData(data);
>>     return resultData;
>>   }
>>
>>    public static <T> ResultData<T> fail(String code ,String message){
>>      ResultData<T> resultData = new ResultData<>();
>>      resultData.setCode(code);
>>      resultData.setMessage(message);
>>      resultData.setData(null);
>>      return resultData;
>>    }
>>   }
>>```

### 三、统一异常处理类
异常类捕捉可以自己使用`try...catch`捕捉，也可以使用`全局异常处理器`进行处理，但是处理的异常类型是具体的，捕捉多个异常还得写多个方法
```java

package com.atguigu.cloud.exp;

import com.atguigu.cloud.resp.ResultData;
import com.atguigu.cloud.resp.ReturnCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author QRH
 * @date 2024/3/15 12:53
 * @description 全局异常处理类
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultData<String> exception(Exception e){
        System.out.println("来到全局异常处理器了");
        log.error("全局异常信息：{}",e.getMessage(),e);
        return ResultData.fail(ReturnCodeEnum.RC500.getCode(), e.getMessage());
    }
}

```

### 四、


