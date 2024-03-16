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

## 第三天

### 一、Consul服务注册与发现
* 为什么要引入服务注册中心？<br>
实现微服务之间的动态注册与发现
  
Consul需要从官网下载（https://developer.hashicorp.com/consul/install） ，安装到本地,验证是否安装成功：
到安装包所在的目录，打开cmd，输入`consul --version`，如果出现一下信息表示成功。

```cmd
F:\Consul>consul --version
Consul v1.18.0
Revision 349cec17
Build Date 2024-02-26T22:05:50Z
Protocol 2 spoken by default, understands 2 to 3 (agent will automatically use protocol >2 when speaking to compatible agents)
```

以开发者模式启动Consul，输入命令`consul agent -dev`。然后就可以访问consul了，访问地址为：`http://localhost:8500`。

### 二、将服务者模块和消费者模块加入Consul
**服务提供者** 步骤：

1、`服务提供者`添加`spring-cloud-starter-consul-discovery`依赖。（项目中cloud-provider-payment8001是提供者）
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-discovery</artifactId>
</dependency>
```
2、提供者的`yml`添加一下配置：
```yml
spring:
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        service-name: ${spring.application.name}
```
3、提供者的`主启动类`上添加`@EnableDiscoveryClient`注解，激活Consul。
```java

package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author QRH
 * @date 2024/3/14 14:15
 * @description 主启动类
 */
@SpringBootApplication
@MapperScan("com.atguigu.cloud.mapper")

@EnableDiscoveryClient

public class Main8001 {
    public static void main(String[] args) {
        SpringApplication.run(Main8001.class,args);
    }
}
```
4、最后在Consul网页内可以查看到服务了。
![img.png](studyImgs/img.png)

**服务消费者** 步骤：

1、消费者模块添加pom依赖：
```xml
 <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-discovery</artifactId>
<!--    排除掉控制打印台的烦人警告，可加可不加-->
    <exclusions>
        <exclusion>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

2、在消费者模块的`yml`中配置：
```yml
spring:
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        service-name: ${spring.application.name}
        prefer-ip-address: true #优先使用服务ip进行注册
```

3、`主启动类`添加`@EnableDiscoveryClient`。
```java
package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author QRH
 * @date 2024/3/15 13:09
 * @description TODO
 */
@SpringBootApplication

@EnableDiscoveryClient

public class Main80 {
    public static void main(String[] args) {
        SpringApplication.run(Main80.class,args);
    }
}

```

最后，启动提供者模块和消费者模块，进行测试。

 http://localhost/consumer/pay/get/1 。提示报错。
```cmd
Caused by: java.net.UnknownHostException: cloud-payment-service
```

造成的原因是，`RestTemplateConfig.java`没有开启`负载均衡`。

```java
package com.atguigu.cloud.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author QRH
 * @date 2024/3/15 13:20
 * @description TODO
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
```

### 三、服务配置与刷新

通用全局配置信息，直接注册进Consul服务器，从Consul获取


步骤：

1、引入依赖 **（服务提供者模块）**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-config</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
</dependency>
```

2、给服务提供者模块添加`bootstrap.yml`
```yml
spring:
  application:
    name: cloud-payment-service
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        service-name: ${spring.application.name}
      config:
        profile-separator: "-"
        format: YAML
```

所以，清除掉`application.yml`中与`bootstrap.yml`中相同的内容。

3、在`application.yml`中添加：
```yml
spring:
  profiles:
    active: dev #多环境配置加载内容dev/prod，不写就是默认default配置
```

### 四、Consul服务器Key/Value配置填写
配置填写一定要遵循官方规则

步骤：

1、Consul页面的`Key/Value`创建文件夹，（必须以`config`开头）
![img.png](studyImgs/img1.png)

2、再在`config`文件夹内创建`服务`,（必须一`/`结尾）
![img.png](studyImgs/img2.png)

3、再给上面三个文件夹创建`data`内容，（data不再是文件夹）
![img.png](studyImgs/img3.png)

### 五、动态刷新
Consul刷新是有默认刷新间隔的，默认是`55秒`。

1、`主启动类`添加`@RefreshScope`

2、`bootstrap.yml`添加配置：（实际开发建议不改）
```yml
spring:
  cloud:
    consul:
      config:
        watch:
          wait-time: 1
```

### 六、Consul的配置持久化

当Consul服务关闭时，再次进入页面之前的配好的配置就会全没有，所以需要将Consul持久化


## LoadBalancer负载均衡
spring cloud LoadBalancer没有专门的jar包，它挂载在`Spring-Cloud-Commons`jar包下。

