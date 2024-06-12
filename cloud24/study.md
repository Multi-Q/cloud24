# SpringCloud学习笔记

## 一、前言

本项目配置要求：

![img_12](studyImgs/img_12.png)

本项目将学习以下技术：

![img_13](studyImgs/img_13.png)

## 二、基本知识

### 2.1 时间格式转化

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

### 2.2 统一返回接口

接口统一返回值：<br>
①思路
> 返回接口的类的标准定义格式：
> > * 2、message描述：本次接口调用的结果描述
> > * 3、data数据：本次返回的数据
>
> 扩展:
>
> > * 1、接口调用时间之类---timestamp：接口调用时间

②步骤
  1、新建枚举类ReturnCodeEnum
>  http请求返回状态码：
> 
>  |分类 |区间 |分类描述|
>  |:-----:|:----:|:-------:|
>  |1***|100~199|信息，服务器收到请求，需要请求者继续执行操作|
>  |2***|200~299|成功，操作被成功接收并处理|
>  |3***|300~399|重定向，需要进一步的操作已完成请求|
>  |4***|400~499|客户端错误，请求包含语法错误或无法完成请求|
>  |5***|500~599|服务器错误，服务器在处理请求的过程中发生了错误|
> 
>  

**ReturnCodeEnum.class**

```java
package com.atguigu.cloud.enums;

import lombok.Getter;

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

}

```

> * 2、定义统一返回对象ResultData

**ResultData.class**

```java
 package com.atguigu.cloud.resp;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author QRH
 * @date 2024/3/15 11:54
 * @description 统一返回数据对象
 */
@Data
@Accessors(chain = true)
public class ResultData<T> {
    private String code;
    /** 结果状态 ,具体状态码参见枚举类ReturnCodeEnum.java*/
    private String message;
    private T data;
    private long timestamp;

    public ResultData() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ResultData<T> success(T data) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(ReturnCodeEnum.RC200.getCode());
        resultData.setMessage(ReturnCodeEnum.RC200.getMessage());
        resultData.setData(data);
        return resultData;
    }

    public static <T> ResultData<T> fail(String code, String message) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(code);
        resultData.setMessage(message);
        resultData.setData(null);
        return resultData;
    }
}
```

### 2.3 统一异常处理类

异常类捕捉可以自己使用`try...catch`捕捉，也可以使用`全局异常处理器`进行处理，但是处理的异常类型是具体的，捕捉多个异常还得写多个方法。

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
    public ResultData<String> exception(Exception e) {
        System.out.println("来到全局异常处理器了");
        log.error("全局异常信息：{}", e.getMessage(), e);
        return ResultData.fail(ReturnCodeEnum.RC500.getCode(), e.getMessage());
    }
}

```

## 三、微服务内容

### 一、 Consul服务注册与发现

* 为什么要引入服务注册中心？<br>
  实现微服务之间的动态注册与发现

Consul需要从官网下载（https://developer.hashicorp.com/consul/install） ，安装到本地,验证是否安装成功： 到安装包所在的目录，打开cmd，输入`consul --version`，如果出现一下信息表示成功。

```cmd
F:\Consul>consul --version
Consul v1.18.0
Revision 349cec17
Build Date 2024-02-26T22:05:50Z
Protocol 2 spoken by default, understands 2 to 3 (agent will automatically use protocol >2 when speaking to compatible agents)
```

以开发者模式启动Consul，输入命令`consul agent -dev`。然后就可以访问consul了，访问地址为：`http://localhost:8500`。

#### 1.1 将服务者模块和消费者模块加入Consul

**服务提供者** 步骤：

1、`服务提供者`添加`spring-cloud-starter-consul-discovery`依赖。（项目中cloud-provider-payment8001是提供者）

```xml

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-discovery</artifactId>
</dependency>
```

2、提供者的`yml`添加以下配置：

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
        SpringApplication.run(Main8001.class, args);
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
        SpringApplication.run(Main80.class, args);
    }
}

```

最后，启动提供者模块和消费者模块，进行测试。

http://localhost/consumer/pay/get/1 。提示报错。

```cmd
Caused by: java.net.UnknownHostException: cloud-payment-service
```

造成的原因是，消费者模块中的`RestTemplateConfig.java`没有开启`负载均衡`。

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
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
```

#### 1.2 服务配置与刷新

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

2、给`服务提供者`模块添加`bootstrap.yml`

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

#### 1.3 Consul服务器Key/Value配置填写

配置填写一定要遵循官方规则。

步骤：

1、Consul页面的`Key/Value`创建文件夹，（必须以`config`开头）
![img.png](studyImgs/img1.png)

2、再在`config`文件夹内创建`服务`,（必须一`/`结尾）。
![img.png](studyImgs/img2.png)

3、再给上面三个文件夹创建`data`内容，（data不再是文件夹）。
![img.png](studyImgs/img3.png)

#### 1.4 动态刷新

Consul刷新是有默认刷新间隔的，默认是`55秒`。

1、`主启动类`添加`@RefreshScope`。

2、`bootstrap.yml`添加配置：（实际开发建议不改）

```yml
spring:
  cloud:
    consul:
      config:
        watch:
          wait-time: 1
```

#### 1.5 Consul的配置持久化

当Consul服务关闭时，再次进入页面之前的配好的配置就会全没有，所以需要将Consul持久化。（持久化配置将在之后进行）


### 二、LoadBalancer负载均衡

spring cloud LoadBalancer没有专门的jar包，它挂载在`Spring-Cloud-Commons`jar包下。

* LB负载均衡是什么？

简单来讲就是将用户的请求平摊的分配到多个服务器上，从而达到系统的HA（高可用），常见的负载均衡有软件Nginx、LVS和硬件F5。

* spring-cloud-starter-loadbalancer是什么？

这是Spring Cloud官方提供的一个开源的、简易的客户端负载均衡器，它包含在Spring Cloud Commons中用来替代以前的Ribbon组件。相较于Ribbon，Spring Cloud LoadBalancer不经能支持`RestTemplate`，还支持`WebClient`（WebClient是Spring Web Flux中提供的功能，可以实现响应式异步请求）。

#### 2.1 完成Consul的数据持久化

**Consul数据持久化配置并注册为Window服务**

步骤：

①在`consul.exe`所在的文件目录下新增一个名为`mydata`的空文件夹（文件夹名叫啥都行）。

②创建`consul_start.bat`并编辑内容（注，consul_start.bat与consul.exe在`同级目录`下）。

```bat
@echo.服务启动...
@echo off
@sc create Consul binpath="F:\Consul\consul.exe agent -server -ui -bind=127.0.0.1 -client=0.0.0.0 -bootstrap-expect 1 -data-dir F:\Consul\mydata\ "
@net start Consul
@sc config Consul start=AUTO
@echo.Consul start is OK......success
@pause
```

③右键consul_start.bat，`以管理员身份打开`。

④验证是否成功，浏览器输入网址(http://localhost:8500)，成功打开，然后在windows的后台看consul的服务是否注册成功。

#### 2.2 开始使用LoadBalancer

步骤：

1、`消费者端`添加`spring-cloud-starter-loadbalancer`(本项目中cloud-sonsumer-order80实消费端)。

```xml

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

2、给`OrderController.java`添加以下内容。

```java
 public static final  String PaymentSrv_URL="http://cloud-payment-service";

@GetMapping(value = "/consumer/pay/get/info")
public String getInfoByConsul(){
    return restTemplate.getForObject(PaymentSrv_URL+"/pay/get/info",String.class);
}

```

3、重启消费者服务，浏览器访问（http://localhost:80/consumer/pay/get/info）。

### 三、OpenFeign服务接口调用

* OpenFeign是什么？

  Feign是一个<span style="color:red;font-weight:bolder;font-size:20px;">`声明式web服务客户端`</span>。他编写web服务客户端变得更容易。`使用Feign创建一个接口并对其进行注释`。它具有可插入的注释支持，包括Feign注释和JAX-RS注释。Feign还支持可插拔编码器和解码器。Spring Cloud添加了对Spring MVC注释的支持，以及对使用Spring Web中默认使用的HttpMessageConveter的支持。Spring Cloud还集成了Eureka、Spring Cloud CircuitBreaker以及Spring Cloud
  LoadBalancer，以便使用Feign时提供负载均衡的http客户端。


* 已经有了loadbalancer为什么还要学OpenFeign？日常用哪个？

  日常用OpenFeign

#### 3.1 OpenFeign通用步骤

①创建一个公共api模块(cloud-consumer-feign-order80)，该模块与服务提供者一一对应。

![img4.jpg](studyImgs/img4.jpg)

②在这个模块的`主启动类`上添加`@EnableFeignClients`表示开启OpenFeign功能并激活

```java
package com.atguigu.cloud;

import com.sun.tools.javac.Main;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author QRH
 * @date 2024/3/21 14:22
 * @description TODO
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class MainOpenFeign80 {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}

```

③将`feign接口`定义在`公共通用模块`（cloud-api-commons）中，并把feign依赖引入。

```xml

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

④新建服务接口PayFeignApi，头上配置`@FeignClient`注解

```java
package com.atguigu.cloud.apis;

import com.atguigu.cloud.entities.PayDTO;
import com.atguigu.cloud.resp.ResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author QRH
 * @date 2024/3/21 14:31
 * @description TODO
 */
@FeignClient(value = "cloud-payment-service")
public interface PayFeignApi {

    @PostMapping(value = "/pay/add/")
    public ResultData addPay(@RequestBody PayDTO payDTO);

    @GetMapping(value = "/pay/get/{id}")
    public ResultData getPayInfo(@PathVariable("id") Integer id);

    @GetMapping(value = "/pay/get/info")
    public String mylb();

    //......
}

```

⑤`cloud-consumer-feign-order80`项目内创建OrderController.java。（该controller与cloud-consumer-order80模块的controller类不同）

```java
package com.atguigu.cloud.controller;

import com.atguigu.cloud.apis.PayFeignApi;
import com.atguigu.cloud.entities.PayDTO;
import com.atguigu.cloud.resp.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * @author QRH
 * @date 2024/3/15 13:22
 * @description TODO
 */
@RestController
public class OrderController {
    @Resource
    private PayFeignApi payFeignApi;

    @PostMapping(value = "/feign/pay/add")
    public ResultData<String> addOrder(@RequestBody PayDTO payDTO) {
        System.out.println("第一步，模拟本地addOrder新增订单成功，第一步在开启addPay支付微服务远程调用");
        return payFeignApi.addPay(payDTO);
    }

    @GetMapping(value = "/feign/pay/get/{id}")
    public ResultData getPayInfo(@PathVariable("id") Integer id) {
        System.out.println("支付微服务远程调用，按照id查询订单支付流水信息");
        return payFeignApi.getPayInfo(id);
    }

    @GetMapping(value = "/feign/pay/info")
    public String getMylb() {
        return payFeignApi.mylb();
    }

}

```

⑤测试，启动服务，观察Consul是否能够注册成功。然后输入网址检查是否能返回正确数据。
![img.png](studyImgs/img6.png)

#### 3.2 OpenFeign高级特性

##### 3.2.1 OpenFeign超时配置

OpenFign默认等待时间：60s，超时报错。

在`消费者模块的application.yml`添加以下内容：

```yml
spring:
  cloud:
    openfeign:
      client:
        config:
          default:
            #连接超时时间
            connectionTimeout: 3000
            #读取超时时间
            readTimeout: 3000
```

上面这种是为全局统一设置超时时间

那为单个服务设置超时时间该如何做呢？

步骤：

①在`cloud-consumer-feign-order80`项目中的controller头上添加指定的`微服务服务实例`。

```java

@RestController
@FeignClient(value = "cloud-payment-service") //指定微服务服务实例 
public class OrderController {
    //.....
}
```

②在yml中配置超时配置

```yml
spring:
  cloud:
    openfeign:
      client:
        config:
          #为单个服务配置超时
          cloud-payment-service:
            connectionTimeout: 3000
            readTimeout: 3000

#          default:
#            #连接超时时间
#            connectionTimeout: 3000
#            #读取超时时间
#            readTimeout: 3000
```

③<span style="color:red;">如果全局超时配置和单个服务超时配置同时共存，会`优先使用单个服务配置的超时时间`。</span>

##### 3.2.2 OpenFign重试机制

重试机制默认是`关闭的`，开启重试机制需写个配置类。

**FeignConfig.java**

```java
package com.atguigu.cloud;

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
        //最大请求次数为3，重试时间间隔为100ms，重试最大间隔时间为1s
        return new Retryer.Default(100, 1, 3);
    }
}

```

OpenFign的重试次数在控制台看不到，只是给出了最终结果。如果想要看到每次重试的结果，将在日志打印那学到。

##### 3.2.3 OpenFign默认HttpClient修改

OpenFign中的Http Client如果不做特殊配置，则会默认使用JDK自带的HttpURLConnection发送HTTP请求。<br>
但，由于默认的HttpURLConnection没有连接池，性能和效率比较低，如果采用默认，性能不是最牛的，所以要加到最大。

所以使用Apache HttpClient5替换HttpURLConnection

步骤：

①修改`消费者模块(cloud-consumer-feign-order80)的pom.xml`，引入`httpclient5`依赖。

```xml
<!-- httpclient5-->
<dependency>
    <groupId>org.apache.httpcomponents.client5</groupId>
    <artifactId>httpclient5</artifactId>
    <version>5.3</version>
</dependency>

<!-- feign-hc5-->
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-hc5</artifactId>
    <version>13.1</version>
</dependency>
```

②修改`消费者模块(cloud-consumer-feign-order80)的application.yml`，配置Apache HttpClient5。

```yml
spring:
  cloud:
    openfign:
      httpclient:
        hc5:
          enabled: true

```

##### 3.2.4 OpenFign请求/压缩功能

对请求和响应进行GZIP压缩，以减少同行过程中的性能损耗。

```yml
spring:
  cloud:
    openfeign:
      compression:
        request:
          enabled: true
          min-request-size: 2048 #最小触发压缩的大小
          mime-types: text/xml,application/xml,application/json #触发压缩数据类型
        response:
          enabled: true
```

##### 3.2.5 OpenFign日志打印功能

日志级别：

* NONE:默认，不显示任何日志。
* BASIC:显示请求方法、请求URL、请求状态码、请求错误信息。
* HEADERS:除了BASIC，还显示请求和响应头信息。
* FULL:除了HEADERS，还显示请求和响应体信息。

所以，

①在`FeignConfig.java`中修改默认日志打印级别。

**FeignConfig.java**

```java
package com.atguigu.cloud;

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


```

②`yml`中配置日志打印级别为`DEBUG`。（Feign日志仅相应DEBUG级别）

写法：`logging.level+含有@FeignClient注解的完整带包名的接口名+debug`

```yml
logging:
  level:
    com:
      atguigu:
        cloud:
          apis:
            PayFeignApi: debug
```

### 四、CirCuitBreaker断路器

断路器：当某个服务不可用时，会自动切换到备用服务。

CirCuitBreaker只是一套规范或接口，落实实现是`Resiliences4j`。

Resiliences4j是什么？

* Resiliences4j是容错库

#### 4.1 熔断（CirCuitBreaker）

##### 4.1.1 按照COUNT_BASE

步骤：

①在提供者模块`cloud-provider-payment8001`新增PayCircuitController.java。

```java
package com.atguigu.cloud.controller;

import cn.hutool.core.util.IdUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author QRH
 * @date 2024/3/27 20:44
 * @description TODO
 */
@RestController
public class PayCircuitController {

    /**
     * Resilience4j circuitBreaker的例子
     * @param id
     * @return
     */
    @GetMapping(value = "/pay/circuit/{id}")
    public String myCircuit(@PathVariable("id") Integer id) {
        if (id == -4) throw new RuntimeException("---circuit id不能为负数");
        if (id == 9999) try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Hello ,Circuit inputId: " + id + " \t" + IdUtil.simpleUUID();
    }
}

```

②修改`PayFeignApi.java`的接口（cloud-api-commons）。

**PayFeignApi.java**

```java

@FeignClient(value = "cloud-payment-service")
public interface PayFeignApi {
    /**
     * 测试熔断 Resilience4j CircuitBreak断路器
     * @param id
     * @return 提示信息
     */
    @GetMapping(value = "/pay/circuit/{id}")
    public String myCircuit(@PathVariable("id") Integer id);
}
```

③消费者模块`cloud-consumer-feign-order80`添加Resilience4j的依赖。

```xml

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>

<!--        由于断路保护需要aop实现，所以必须导入aop包-->
<dependency>
    <groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-aop</artifactId>
</dependency>

```

④编写yml。

```yml
spring:
  cloud:
    openfeign:
      #开启断路器和分组激活spring.cloud.openfeign.circuitbreaker.enabled
      circuitbreaker:
        enabled: true
        group:
          enabled: true #没开分组永远不用开分组配置。精确优先，分组次之，默认最后

resilience4j:
  circuitbreaker:
    configs:
      default:
        failureRateThreshold: 50 #设置50%的调用失败时打开断路器，超过失败请求百分比CirCuitBreaker变为OPEN状态
        slidingWindowType: COUNT_BASED #滑动窗口的类型
        slidingWindowSize: 6 #滑动窗口的大小配置COUNT_BASED表示6个请求，配置TIME_BASED表示6秒
        minimumNumberOfCalls: 6 #断路器计算失败率或慢调用率之前所需的最小样本（每个滑动周期）。默认为10，
        automaticTransitionFromOpenToHalfOpenEnabled: true #是否启用自动从开启状态过渡到半开状态，默认值为true
        permittedNumberOfCallsInHalfOpenState: 2 #半开状态允许的最大请求数，默认为10
        recordExceptions:
          - java.lang.Exception
    instances:
      cloud-payment-service:
        baseConfig: default #使用默认配置
```

⑤新建OrderCircuitController.java。

```java
package com.atguigu.cloud.controller;

import com.atguigu.cloud.apis.PayFeignApi;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author QRH
 * @date 2024/3/27 21:17
 * @description TODO
 */
@RestController
public class OrderCircuitController {
    @Resource
    private PayFeignApi payFeignApi;

    @GetMapping(value = "/feign/pay/circuit/{id}")
    @CircuitBreaker(name = "cloud-payment-service", fallbackMethod = "myCircuitFallback")
    public String myCircuitBreaker(@PathVariable("id") Integer id) {
        return payFeignApi.myCircuit(id);
    }

    //myCircuitFallback就是服务熔断降级后的兜底处理方法
    public String myCircuitFallback(Integer id, Throwable t) {
        return "myCircuitFallback，系统繁忙，请稍后重试----~~~~";
    }
}

```

⑥测试

##### 4.1.2 按照TIME_BASED

步骤： ①修改yml。

```yml
resilience4j:
  timelimiter:
    configs:
      default:
        timeout-duration: 10s #默认限制远程1s，超过1s就超时异常，配置了降级，就走降级逻辑
  circuitbreaker:
    configs:
      default:
        #TIME_BASED
        failureRateThreshold: 50 #设置50%的调用失败时打开断路器，超过失败请求百分⽐CircuitBreaker变为OPEN状态。
        slowCallDurationThreshold: 2s #慢调用时间阈值，高于这个阈值的视为慢调用并增加慢调用比例。
        slowCallRateThreshold: 30 #慢调用百分比峰值，断路器把调用时间⼤于slowCallDurationThreshold，视为慢调用，当慢调用比例高于阈值，断路器打开，并开启服务降级
        slidingWindowType: TIME_BASED # 滑动窗口的类型
        slidingWindowSize: 2 #滑动窗口的大小配置，配置TIME_BASED表示2秒
        minimumNumberOfCalls: 2 #断路器计算失败率或慢调用率之前所需的最小样本(每个滑动窗口周期)。
        permittedNumberOfCallsInHalfOpenState: 2 #半开状态允许的最大请求数，默认值为10。
        waitDurationInOpenState: 5s #从OPEN到HALF_OPEN状态需要等待的时间

        recordExceptions:
          - java.lang.Exception
    instances:
      cloud-payment-service:
        baseConfig: default #使用默认配置
```

##### 4.1.3 COUNT_BASED和TIME_BASED用哪个？

建议使用COUNT_BASED。

#### 4.2 隔离（BuldHead）

隔离是什么？

* 限制并发。

隔离能干什么？

* 用来限制对于下游服务的并发请求数。

Resilience4j提供了两种隔离的实现：

##### 4.2.1 Semahore信号量

使用Semahore需要导入舱壁的包

```xml

<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-bulkhead</artifactId>
</dependency>
```

然后配置

```yml
resilience4j:
  bulkhead:
    configs:
      default:
        maxConcurrentCalls: 2
        maxWaitDuration: 1s
      instances:
        cloud-payment-service:
          baseConfig: default
  timelimiter:
    configs:
      default:
        timeout-duration: 10s #默认限制远程1s，超过1s就超时异常，配置了降级，就走降级逻辑
```

再在提供者模块`cloud-provider-payment8001`中添加方法。
**PayCircuitController.java**

```java

/**
 * Resilience4j bulkHead的例子
 * @param id
 * @return
 */
@GetMapping(value = "/pay/bulkhead/{id}")
public String myBulkHead(@PathVariable("id") Integer id){
    if(id==-4)throw new RuntimeException("----bulkHead id 不能为空");
    if(id==999){
        try{
            TimeUnit.SECONDS.sleep(5);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
    return"Hello bulkHead inputId : "+id+"\t"+IdUtil.simpleUUID();
}

```

PayFeignApi.java添加myBulkHead方法，供外部调用。

```java
/**
 * 测试熔断 Resilience4j BulkHead
 * @param id
 * @return 提示信息
 */
@GetMapping(value = "/pay/bulkhead/{id}")
public String myBulkHead(@PathVariable("id") Integer id);
```

消费者模块`cloud-consumer-feign-order80`中添加方法访问。
**OrderCircuitController.java**

```java
   /**
 * 舱壁
 * @param id
 * @return
 */
@GetMapping(value = "/feign/pay/bulkhead/{id}")
@Bulkhead(name = "cloud-payment-service", fallbackMethod = "myBulkheadFallback", type = Bulkhead.Type.SEMAPHORE)
public String myBulkHead(@PathVariable("id") Integer id){
    return payFeignApi.myBulkHead(id);
}

public String myBulkheadFallback(Integer id,Throwable t){
    return"myBulkheadFallback ，舱壁超出最大数量限制， 系统繁忙，请稍后重试----~~~~";
}

```

最后启动项目，测试结果。

##### 4.2.2 固定线程池FixedThreadPoolBulkhead舱壁

使用Semahore需要导入舱壁的包。

```xml

<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-bulkhead</artifactId>
</dependency>
```

然后配置

```yml
resilience4j:
  thread-pool-bulkhead:
    configs:
      default:
        core-thread-pool-size: 1
        max-thread-pool-size: 1
        queue-capacity: 1
    instances:
      cloud-payment-service:
        base-config: default
  timelimiter:
    configs:
      default:
        timeout-duration: 10s #默认限制远程1s，超过1s就超时异常，配置了降级，就走降级逻辑

#使用固定线程需要将spring.cloud.openfeign.circuitbreaker.group.enabled设置为false
```

再在提供者模块`cloud-provider-hystrix-payment8001`中添加方法。
**PayCircuitController.java**

```java

/**
 * Resilience4j bulkHead的例子
 * @param id
 * @return
 */
@GetMapping(value = "/pay/bulkhead/{id}")
public String myBulkHead(@PathVariable("id") Integer id){
    if(id==-4)throw new RuntimeException("----bulkHead id 不能为空");
    if(id==999)try{TimeUnit.SECONDS.sleep(5);}catch(InterruptedException e){e.printStackTrace();}
    return"Hello bulkHead inputId : "+id+"\t"+IdUtil.simpleUUID();
}

```

PayFeignApi.java添加myBulkHead方法，供外部调用。

```java
/**
 * 测试熔断 Resilience4j BulkHead
 * @param id
 * @return 提示信息
 */
@GetMapping(value = "/pay/bulkhead/{id}")
public String myBulkHead(@PathVariable("id") Integer id);
```

消费者模块`cloud-consumer-feign-order80`中添加方法访问。
**OrderCircuitController.java**

```java
   /**
 * 舱壁
 * @param id
 * @return
 */
@GetMapping(value = "/feign/pay/bulkhead/{id}")
@Bulkhead(name = "cloud-payment-service", fallbackMethod = "myBulkheadFallback", type = Bulkhead.Type.SEMAPHORE)
public String myBulkHead(@PathVariable("id") Integer id){
    return payFeignApi.myBulkHead(id);
}

public String myBulkheadFallback(Integer id,Throwable t){
    return"myBulkheadFallback ，舱壁超出最大数量限制， 系统繁忙，请稍后重试----~~~~";
}

```

最后启动项目，测试结果。

#### 4.3 限流器（RateLimiter）

限流器是什么？

* 用来限制对某个资源（如：接口）的访问次数。

```xml

<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-ratelimiter</artifactId>
</dependency>
```

```yml
 #限流器
 resilience4j:
   ratelimiter:
     configs:
       default:
         limit-for-period: 2
         limit-refresh-period: 1s
         timeout-duration: 1
     instances:
       cloud-payment-service:
         base-config: default
```

### 五、分布式链路追踪

为什么要用分布式链路追踪？

* 在微服务框架中，一个由客户端发起的请求在后端系统中会经过多个不同的服务结点调用来协同产生最后的请求结果，每一个前段请求都会形成一条复杂的分布式服务调用链路，链路中的任何一环出现高延时或错误都会引起整个请求最后的失败。

#### 5.1 Zipkin链路追踪负责数据展现

* Zipkin是一个开源的分布式追踪系统，用于收集和聚合跨服务调用的链路和操作数据，可用于构建和操作分布式系统间的延迟数据。

zipkin下载地址：https://zipkin.io/pages/quickstart.html

cmd窗口下执行：`java -jar zipkin-server-3.1.1-exec.jar`

访问地址：http://localhost:9411/，若能出现ui界面说明成功了。

#### 5.2 Micrometer+Zipkin搭配使用

1、引入相关jar。

**父工程pom**

```xml

<prperties>
    <micrometer-tracing.version>1.2.0</micrometer-tracing.version>
    <micrometer-observation.version>1.12.0</micrometer-observation.version>
    <feign-micrometer.version>12.5</feign-micrometer.version>
    <zipkin-reporter-brave.version>2.17.0</zipkin-reporter-brave.version>
</properties>

<dependencyManagement>
<dependencies>
    <!--micrometer-tracing-bom导入链路追踪版本中心-1-->
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-tracing-bom</artifactId>
        <version>${micrometer-tracing.version}</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
    <!--            micrometer-tracing指标追踪-2-->
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-tracing</artifactId>
        <version>${micrometer-tracing.version}</version>
    </dependency>
    <!--micrometer-tracing-bridge-brave适配zipkin的桥接包 3-->
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-tracing-bridge-brave</artifactId>
        <version>${micrometer-tracing.version}</version>
    </dependency>
    <!--micrometer-observation 4-->
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-observation</artifactId>
        <version>${micrometer-observation.version}</version>
    </dependency>
    <!--feign-micrometer 5-->
    <dependency>
        <groupId>io.github.openfeign</groupId>
        <artifactId>feign-micrometer</artifactId>
        <version>${feign-micrometer.version}</version>
    </dependency>
    <!--zipkin-reporter-brave 6-->
    <dependency>
        <groupId>io.zipkin.reporter2</groupId>
        <artifactId>zipkin-reporter-brave</artifactId>
        <version>${zipkin-reporter-brave.version}</version>
    </dependency>
</dependencies>
</dependencyManagement>

```

2、`服务提供者8001(cloud-payment-service)`。
**pom**

```xml
 <!--            micrometer-tracing指标追踪-2-->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing</artifactId>
    <version>${micrometer-tracing.version}</version>
</dependency>

        <!--micrometer-tracing-bridge-brave适配zipkin的桥接包 3-->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
    <version>${micrometer-tracing.version}</version>
</dependency>

        <!--micrometer-observation 4-->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-observation</artifactId>
    <version>${micrometer-observation.version}</version>
</dependency>

        <!--feign-micrometer 5-->
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-micrometer</artifactId>
    <version>${feign-micrometer.version}</version>
</dependency>

        <!--zipkin-reporter-brave 6-->
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
    <version>${zipkin-reporter-brave.version}</version>
</dependency>
```

**application.yml**

```yml
management:
  zipkin:
    tracing:
      endpoint: http://localhost:9411/api/v2/spans
  tracing:
    sampling:
      probability: 1.0 #值越大手机越及时
```

3、新建业务类。
**PayMicrometerController.java**

```java
import cn.hutool.core.util.IdUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author QRH
 * @date 2024/4/1 17:46
 * @description 测试Micrometer
 */
@RestController
public class PayMicrometerController {
    @GetMapping(value = "/pay/micrometer/{id}")
    public String myMicrometer(@PathVariable("id") Integer id) {
        return "欢迎来到myMicrometer inputId ： " + id + "\t 服务返回：" + IdUtil.simpleUUID();
    }
}
```

**PayFeignApi.java**

```java

/**
 * Micrometer链路追踪
 *
 * @param id
 * @return
 */
@GetMapping(value = "/pay/micrometer/{id}")
public String myMicrometer(@PathVariable("id") Integer id);
```

3、`服务消费者80(cloud-consumer-feign-feign-order80)`
操作步骤如前所示。

### 六、网关

#### 6.1 配置

1、新建cloud-gateway-gateway9527。

2、pom.xml。

```xml

<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-consul-discovery</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>


</dependencies>

<build>
<plugins>
    <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
    </plugin>
</plugins>
</build>

```

3、application.yml。

```yml

server:
  port: 9527

spring:
  application:
    name: cloud-gateway
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        prefer-ip-address: true
        service-name: ${spring.application.name}

```

4、主启动。

```java
package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author QRH
 * @date 2024/4/1 18:25
 * @description TODO
 */
@SpringBootApplication
@EnableDiscoveryClient
public class Main9527 {
    public static void main(String[] args) {
        SpringApplication.run(Main9527.class, args);
    }
}


```

5、关联路由。

```yml
spring:
  cloud:
    gateway:
      routes:
        - id: pay_routh1 #pay_routh1                #路由的ID(类似mysql主键ID)，没有固定规则但要求唯一，建议配合服务名
          uri: http://localhost:8001                #匹配后提供服务的路由地址
          predicates:
            - Path=/pay/gateway/get/**              # 断言，路径相匹配的进行路由


        - id: pay_routh2 #pay_routh2                #路由的ID(类似mysql主键ID)，没有固定规则但要求唯一，建议配合服务名
          uri: http://localhost:8001                #匹配后提供服务的路由地址
          predicates:
            - Path=/pay/gateway/info/**              # 断言，路径相匹配的进行路由
```

6、启动使用9527端口访问链接。

7、FeignApi.java添加两个接口。

```java

@GetMapping(value = "/pay/gateway/get/{id}")
public ResultData getGateWayById(@PathVariable("id") Integer id);

@GetMapping(value = "/pay/gateway/get/info")
public ResultData<String> getGateWayInfo();
```

8、PayGateWayController.java添加两个接口

```java
package com.atguigu.cloud.controller;

import cn.hutool.core.util.IdUtil;
import com.atguigu.cloud.entities.Pay;
import com.atguigu.cloud.resp.ResultData;
import com.atguigu.cloud.service.PayService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author QRH
 * @date 2024/4/1 22:16
 * @description TODO
 */
@RestController
public class PayGateWayController {

    @Resource
    private PayService payService;

    @GetMapping(value = "/pay/gateway/get/{id}")
    public ResultData<Pay> getGateWayById(@PathVariable("id") Integer id) {
        return ResultData.success(payService.getById(id));
    }

    @GetMapping(value = "/pay/gateway/get/info")
    public ResultData<String> getGateWayInfo() {
        return ResultData.success("gateway info test: " + IdUtil.simpleUUID());
    }

}


```

9、OrderGateWayController.java添加两个接口。

```java
package com.atguigu.cloud.controller;

import cn.hutool.core.util.IdUtil;
import com.atguigu.cloud.apis.PayFeignApi;
import com.atguigu.cloud.resp.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author QRH
 * @date 2024/4/1 22:31
 * @description TODO
 */
@RestController
public class OrderGateWayController {

    @Resource
    private PayFeignApi payFeignApi;

    @GetMapping(value = "/feign/pay/gateway/get/{id}")
    public ResultData getGateWayById(@PathVariable("id") Integer id) {
        return payFeignApi.getGateWayById(id);
    }

    @GetMapping(value = "/feign/pay/gateway/get/info")
    public ResultData<String> getGateWayInfo() {
        return payFeignApi.getGateWayInfo();
    }

}

```

```java
/**
	这里不再是通过微服务实例名来远程调用提供者的相关方法了，而是使用网关来进行代理请求，只需要在配置文件中配置好断言就可实现远程调用
*/
//@FeignClient(value = "cloud-payment-service")
@FeignClient(value = "cloud-gateway")
public interface PayFeignApi {
}
```

#### 6.2 Predicate常用api

##### 6.2.1 After Route Predicate

在`什么时间之后`能访问这个链接。

```yml
spring:
  cloud:
    gateway:
      routes:
        - id: pay_routh1 #pay_routh1                #路由的ID(类似mysql主键ID)，没有固定规则但要求唯一，建议配合服务名
          uri: lb://cloud-payment-service               #匹配后提供服务的路由地址
          predicates:
            - Path=/pay/gateway/get/**
            - After=2024-04-01T00:00:00.000+08:00[Asia/Shanghai]
```

##### 6.2.2 Before Route Predicate

在`什么时间之前`能访问这个链接。

```yml
spring:
  cloud:
    gateway:
      routes:
        - id: pay_routh1 #pay_routh1                #路由的ID(类似mysql主键ID)，没有固定规则但要求唯一，建议配合服务名
          uri: lb://cloud-payment-service               #匹配后提供服务的路由地址
          predicates:
            - Path=/pay/gateway/get/**
            - Before=2024-04-03T00:00:00.000+08:00[Asia/Shanghai]
```

##### 6.2.3 Between Route Predicate

在`什么时间之前`能访问这个链接。

```yml
spring:
  cloud:
    gateway:
      routes:
        - id: pay_routh1 #pay_routh1                #路由的ID(类似mysql主键ID)，没有固定规则但要求唯一，建议配合服务名
          uri: lb://cloud-payment-service               #匹配后提供服务的路由地址
          predicates:
            - Path=/pay/gateway/get/**
            - Between=2024-04-02T00:00:00.000+08:00[Asia/Shanghai],2024-04-03T00:00:00.000+08:00[Asia/Shanghai]
```

##### 6.2.4 Cookie Route Predicate

Cookie断言，需要两个参数`Cookie`和`正则表达式`。

```yml
spring:
  cloud:
    gateway:
      routes:
        - id: pay_routh1 #pay_routh1                #路由的ID(类似mysql主键ID)，没有固定规则但要求唯一，建议配合服务名
          uri: lb://cloud-payment-service               #匹配后提供服务的路由地址
          predicates:
            - Path=/pay/gateway/get/**
            - After=2024-04-01T00:00:00.000+08:00[Asia/Shanghai]
            - Cookie=username,qrh
```

结果，使用cmd测试。

```cmd
C:\Users\qrh19>curl http://localhost:9527/pay/gateway/get/1 --cookie "username=qrh"

{"code":"200","message":"success","data":{"id":1,"payNo":"pay17203699","orderNo":"6544bafb424a","userId":1,"amount":19.99,"deleted":0,"createTime":"2024-03-14 12:56:24","updateTime":"2024-03-14 15:18:14"},"timestamp":1712069458399}
```

##### 6.2.5 Header Route Predicate

需要两个参数`header请求头`和`正则表达式`。

```yml
spring:
  cloud:
    gateway:
      routes:
        - id: pay_routh1 #pay_routh1                #路由的ID(类似mysql主键ID)，没有固定规则但要求唯一，建议配合服务名
          uri: lb://cloud-payment-service               #匹配后提供服务的路由地址
          predicates:
            - Path=/pay/gateway/get/**
            - After=2024-04-01T00:00:00.000+08:00[Asia/Shanghai]
            - Header=X-Request-Id,123456 
```

结果，使用cmd测试。

```cmd
C:\Users\qrh19>curl http://localhost:9527/pay/gateway/get/1 -H "X-Request-Id:123456"

{"code":"200","message":"success","data":{"id":1,"payNo":"pay17203699","orderNo":"6544bafb424a","userId":1,"amount":19.99,"deleted":0,"createTime":"2024-03-14 12:56:24","updateTime":"2024-03-14 15:18:14"},"timestamp":1712070083153}

```

##### 6.2.6 Host Route Predicate

需要两个参数`header请求头`和`正则表达式`。

```yml
spring:
  cloud:
    gateway:
      routes:
        - id: pay_routh1 #pay_routh1                #路由的ID(类似mysql主键ID)，没有固定规则但要求唯一，建议配合服务名
          uri: lb://cloud-payment-service               #匹配后提供服务的路由地址
          predicates:
            - Path=/pay/gateway/get/**
            - Host=**.atguigu.com
```

结果，使用cmd测试

```cmd
C:\Users\qrh19>curl http://localhost:9527/pay/gateway/get/1 -H "Host:www.atguigu.com"

{"code":"200","message":"success","data":{"id":1,"payNo":"pay17203699","orderNo":"6544bafb424a","userId":1,"amount":19.99,"deleted":0,"createTime":"2024-03-14 12:56:24","updateTime":"2024-03-14 15:18:14"},"timestamp":1712070408884}

```

##### 6.2.7 Method Route Predicate

```yml
spring:
  cloud:
    gateway:
      routes:
        - id: pay_routh1 #pay_routh1                #路由的ID(类似mysql主键ID)，没有固定规则但要求唯一，建议配合服务名
          uri: lb://cloud-payment-service               #匹配后提供服务的路由地址
          predicates:
            - Path=/pay/gateway/get/**
            - Method=GET,POST #注意需要大写，小写不行
```

结果，使用cmd测试。

```cmd
C:\Users\qrh19>curl -X GET  http://localhost:9527/pay/gateway/get/1

{"code":"200","message":"success","data":{"id":1,"payNo":"pay17203699","orderNo":"6544bafb424a","userId":1,"amount":19.99,"deleted":0,"createTime":"2024-03-14 12:56:24","updateTime":"2024-03-14 15:18:14"},"timestamp":1712070926046}
```

##### 6.2.8 Path Route Predicate

访问路径。

```yml
spring:
  cloud:
    gateway:
      routes:
        - id: pay_routh1 #pay_routh1                #路由的ID(类似mysql主键ID)，没有固定规则但要求唯一，建议配合服务名
          uri: lb://cloud-payment-service               #匹配后提供服务的路由地址
          predicates:
            - Path=/pay/gateway/get/**

```

##### 6.2.9 Query Route Predicate

查询请求参数。

```yml
spring:
  cloud:
    gateway:
      routes:
        - id: pay_routh1 #pay_routh1                #路由的ID(类似mysql主键ID)，没有固定规则但要求唯一，建议配合服务名
          uri: lb://cloud-payment-service               #匹配后提供服务的路由地址
          predicates:
            - Path=/pay/gateway/get/**
            - Query=username,qrh
```

结果，使用cmd测试

```cmd
C:\Users\qrh19>curl http://localhost:9527/pay/gateway/get/1?username=qrh

{"code":"200","message":"success","data":{"id":1,"payNo":"pay17203699","orderNo":"6544bafb424a","userId":1,"amount":19.99,"deleted":0,"createTime":"2024-03-14 12:56:24","updateTime":"2024-03-14 15:18:14"},"timestamp":1712071157949}
C:\Users\qrh19>

```

##### 6.2.10 RemoteAddr  Route Predicate

远程地址请求访问，只有这个地址才能访问。

```yml
spring:
  cloud:
    gateway:
      routes:
        - id: pay_routh1 #pay_routh1                #路由的ID(类似mysql主键ID)，没有固定规则但要求唯一，建议配合服务名
          uri: lb://cloud-payment-service               #匹配后提供服务的路由地址
          predicates:
            - Path=/pay/gateway/get/**
            - RemoteAddr=192.168.10.1/24
```

结果，使用cmd测试

```cmd
C:\Users\qrh19>curl http://192.168.10.12:9527/pay/gateway/get/1

{"code":"200","message":"success","data":{"id":1,"payNo":"pay17203699","orderNo":"6544bafb424a","userId":1,"amount":19.99,"deleted":0,"createTime":"2024-03-14 12:56:24","updateTime":"2024-03-14 15:18:14"},"timestamp":1712071431679}

```

##### 6.2.11 自定义断言

①新建自定义断言类，（注意：`必须以RoutePredicateFactory`结尾）。

**MyRoutePredicateFactory.java**

```java
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
                String userType = serverWebExchange.getRequest().getQueryParams().getFirst("userType");
                if (userType == null) return false;
                if (userType.equalsIgnoreCase(config.getUserType())) return true;

                return false;
            }

//            public Object getConfig() {
//                return config;
//            }

//            public String toString() {
//                return String.format("MyRoutePredicateFactory: %s", config.getUserType());
//            }

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

```

②写yml。

```yml
spring:
  cloud:
    gateway:
      routes:
        - id: pay_routh1 #pay_routh1                #路由的ID(类似mysql主键ID)，没有固定规则但要求唯一，建议配合服务名
          uri: lb://cloud-payment-service               #匹配后提供服务的路由地址
          predicates:
            - Path=/pay/gateway/get/**
            - My=diamod
```

③测试

```cmd

C:\Users\qrh19>curl http://localhost:9527/pay/gateway/get/1?userType=diamod

{"code":"200","message":"success","data":{"id":1,"payNo":"pay17203699","orderNo":"6544bafb424a","userId":1,"amount":19.99,"deleted":0,"createTime":"2024-03-14 12:56:24","updateTime":"2024-03-14 15:18:14"},"timestamp":1712073665745}

```

#### 6.3 Filter

相当于Spring MVC的拦截器，Serlvet的过滤器。

##### 6.3.1 全局过滤器Global Filter

gateway默认自带的，直接用就可以。

##### 6.3.2 单一过滤器

单一内置过滤器一共有38个。

###### 6.3.2.1 请求头过滤器

1、AddRequestHeader GatewayFilter Factory

该过滤器包含一个`name`和`value`。

步骤：

①提供者模块(cloud-provider-payment8001) PayGateWayController.java添加方法

```java
 @GetMapping(value = "/pay/gateway/filter")
public ResultData<String> getGateWayFilter(HttpServletRequest request){
    String result="";
    Enumeration<String> headers=request.getHeaderNames();
    while(headers.hasMoreElements()){
        String headerName=headers.nextElement();
        String headerValue=request.getHeader(headerName);
        System.out.println("请求头名： "+headerName+"\t\t\t请求头值： "+headerValue);
        if(headerName.equalsIgnoreCase("X-Request-atguigu1")||headerName.equalsIgnoreCase("X-Request-atguigu2")){
            result=result+headerName+"\t"+headerValue+" ";
        }

    }
    return ResultData.success("getGateWayFilter 过滤器 test： "+result+" \t"+DateUtil.now());
}

```

②cloud-gateway9527 yml编写配置。

```yml
spring:
  cloud:
    gateway:
      routes:

        - id: pay_routh3
          uri: lb://cloud-payment-service
          predicates:
            - Path=/pay/gateway/filter/**
          filters:
            - AddRequestHeader=X-Request-atguigu1,atguiguValue1
            - AddRequestHeader=X-Request-atguigu2,atguiguValue2

```

2 RemoveRequestHeader GatewayFilter Factory

```yml
spring:
  cloud:
    gateway:
      routes:

        - id: pay_routh3
          uri: lb://cloud-payment-service
          predicates:
            - Path=/pay/gateway/filter/**
          filters:
            - AddRequestHeader=X-Request-atguigu1,atguiguValue1
            - AddRequestHeader=X-Request-atguigu2,atguiguValue2
            - RemoveRequestHeader=X-Request-atguigu1
```

3、 SetRequestHeader GatewayFilter Factory

```yml
spring:
  cloud:
    gateway:
      routes:

        - id: pay_routh3
          uri: lb://cloud-payment-service
          predicates:
            - Path=/pay/gateway/filter/**
          filters:
            - AddRequestHeader=X-Request-atguigu1,atguiguValue1
            - AddRequestHeader=X-Request-atguigu2,atguiguValue2
            #设置或修改，如果有这个name那么就修改，否则是新增
            - SetRequestHeader=X-Request-atguigu2,HelloWorld
            - SetRequestHeader=X-request-atguigu3,guiguValue3
```

###### 6.3.2.2 请求参数过滤器

1、AddRequestParameter和RemoveRequestParameter。

```java
@GetMapping(value = "/pay/gateway/filter")
public ResultData<String> getGateWayFilter(HttpServletRequest request){
    String result="";
    Enumeration<String> headers=request.getHeaderNames();
    while(headers.hasMoreElements()){
        String headerName=headers.nextElement();
        String headerValue=request.getHeader(headerName);
        System.out.println("请求头名： "+headerName+"\t\t\t请求头值： "+headerValue);
        if(headerName.equalsIgnoreCase("X-Request-atguigu1")
           ||headerName.equalsIgnoreCase("X-Request-atguigu2")){
            result=result+headerName+"\t"+headerValue+" ";
        }

    }
    System.out.println("=============================");
    String customerId=request.getParameter("customerId");
    System.out.println("request parameter customId: "+customerId);

    String customerName=request.getParameter("customerName");
    System.out.println("request parameter customerName: "+customerName);
    System.out.println("=============================");
    return ResultData.success("getGateWayFilter 过滤器 test： "+result+" \t"+DateUtil.now());
}

```

```yml
spring:
  cloud:
    gateway:
      routes:

        - id: pay_routh3
          uri: lb://cloud-payment-service
          predicates:
            - Path=/pay/gateway/filter/**
          filters:
            - AddRequestHeader=X-Request-atguigu1,atguiguValue1
            - AddRequestHeader=X-Request-atguigu2,atguiguValue2
            #设置或修改，如果有这个name那么就修改，否则是新增
            - AddRequestParameter=customerId,1234566
            - RemoveRequestParameter=customerName
```

访问http://localhost:9527/pay/gateway/filter?customerId=9999&customerName=h123，如果传了customerId，那就是用传过来的值，如果链接没有请求参数，那就使用配置内定义好的值。

![img7.png](studyImgs/img7.png)

![img8.png](studyImgs/img8.png)

##### 6.3.3 自定义全局过滤器

步骤：

①新建一个MyGlobalFilter.java。

```java
package com.atguigu.cloud.mygateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author QRH
 * @date 2024/4/3 17:35
 * @description 自定义全局过滤器
 */
@Component
@Slf4j
public class MyGlobalFilter implements GlobalFilter, Ordered {
    private static final String BEGIN_VISIT_TIME = "begin_visit_time";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //记录接口开始访问时间
        exchange.getAttributes().put(BEGIN_VISIT_TIME, System.currentTimeMillis());
        // 返回统计的各个接口给后台
        return chain.filter(exchange)
                .then(
                        Mono.fromRunnable(() -> {
                            Long beginVisitTime = exchange.getAttribute(BEGIN_VISIT_TIME);
                            if (beginVisitTime != null) {
                                log.info("访问接口主机： " + exchange.getRequest().getURI().getHost());
                                log.info("访问接口端口： " + exchange.getRequest().getURI().getPort());
                                log.info("访问接口URL： " + exchange.getRequest().getURI().getPath());
                                log.info("访问接口URL后面的参数： " + exchange.getRequest().getURI().getRawQuery());
                                log.info("访问接口时长： " + (System.currentTimeMillis() - beginVisitTime) + "毫秒");
                                log.info("=====================================");
                                System.out.println();
                            }
                        })
                );
    }

    /**
     * 数字越小，优先级越高
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}

```

##### 6.3.4 自定义条件过滤器

步骤：

①新建一个MyGatewayFilterFactory.java（必须以GatewayFilterFactory结尾），并继承AbstractGatewayFilterFactory。

### 七、Spring Cloud Alibaba

* Spring Cloud Alibaba的版本不是最新的。



**SpringBoot和Spring Alibaba Cloud版本对应关系：**

![img_24.png](studyImgs/img_24.png)



**Spring Cloud Alibaba 集成的组件版本:**

![img_25.png](studyImgs/img_25.png)



#### 7.1 Spring Cloud Alibaba Nacos

Nacos是一个动态服务发现、配置管理、服务管理平台，Nacos 致力于解决微服务治理中的问题。Nacos 提供了服务注册、服务发现、配置管理、服务管理、服务网关等微服务治理功能，并支持基于 Spring Cloud 构建微服务应用。

* Nacos作为注册中心，可以替代Eureka，作为配置中心，可以替代Config

Nacos=Spring Cloud Consul

1、安装下载

https://nacos.io/zh-cn/docs/quick-start.html

2、启动Nacos

* Windows版本启动命令：startup.cmd -m standalone
* Linux版本启动命令：sh startup.sh -m standalone

启动后，访问http://localhost:8848/nacos/index.html，成功则配置没错

![img_1.png](studyImgs/img_1.png)

##### 7.1.1 服务注册中心

步骤：

①新建一个cloudalibaba-provider-payment9001模块(提供者模块)。

②加pom。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>cloud24</artifactId>
        <groupId>com.atguigu.cloud</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>cloudalibaba-provider-payment9001</artifactId>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project-build-sourceEncoding>UTF-8</project-build-sourceEncoding>
    </properties>

    <dependencies>

        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>

        <dependency>
            <groupId>com.atguigu.cloud</groupId>
            <artifactId>cloud-api-commons</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>


    </dependencies>

</project>

```

③写yml。

```yaml
server:
  port: 9001


spring:
  application:
    name: nacos-payment-provider
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848 #配置nacos地址
```

④主启动。

```java
package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author QRH
 * @date 2024/4/4 0:06
 * @description TODO
 */
@SpringBootApplication
@EnableDiscoveryClient
public class Main9001 {
    public static void main(String[] args) {
        SpringApplication.run(Main9001.class, args);
    }
}

```

⑤controller。

```java
package com.atguigu.cloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author QRH
 * @date 2024/4/4 0:10
 * @description TODO
 */
@RestController
public class PayAlibabaController {

    @Value("${server.port}")
    private String serverPort;

    @GetMapping(value = "/pay/nacos/{id}")
    public String getPayInfo(@PathVariable("id") Integer id) {
        return "nacos registry, serverPort: " + serverPort + "\t id" + id;
    }

}

```

消费者模块的pom与提供者模块差不太多。

消费者模块：

```xml
<dependecies>
    
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-loadbalancer</artifactId>
    </dependency>
    
    
</dependecies>

```

```yml
server:
  port: 83

spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848

#消费者将要访问的微服务名称（nacos微服务提供者叫什么就写什么）
service-url:
  nacos-user-service: http://nacos-payment-provider


```

配置RestTemplate。

```java
package com.atguigu.cloud.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author QRH
 * @date 2024/4/4 0:26
 * @description TODO
 */
@Configuration
public class RestTemplateConfig {
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}

```

controller。

```java
package com.atguigu.cloud.controller;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author QRH
 * @date 2024/4/4 0:34
 * @description TODO
 */
@RestController
public class OrderNacosController {
    @Resource
    private RestTemplate restTemplate;

    @Value("${service-url.nacos-user-service}")
    private String serverURL;

    @GetMapping(value = "/consumer/pay/nacos/{id}")
    public String paymentInfo(@PathVariable("id") Integer id) {
        String res = restTemplate.getForObject(serverURL + "/pay/nacos/" + id, String.class);

        return res + "\t我是OrderNacosController83调用者......";
    }
}


```

##### 7.1.2 服务配置中心

①新建模块(cloudalibaba-config-nacos-client3377)。

②改pom。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>cloud24</artifactId>
        <groupId>com.atguigu.cloud</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>cloudalibaba-config-nacos-client3377</artifactId>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bootstrap</artifactId>
        </dependency>
        <!--nacos-config-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
        </dependency>

        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>


    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

③写yml。

**bootstrap.yml**

```yml
spring:
  application:
    name: nacos-config-client

  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848 #nacos服务注册中心地址
      config:
        server-addr: localhost:8848 #nacos作为配置中心的地址
        file-extension: yaml #指定yml格式


```

**application.yml**

```yml
server:
  port: 3377

spring:
  profiles:
    active: dev

```

④主启动。

```java
package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
public class Main3377 {
    public static void main(String[] args) {
        SpringApplication.run(Main3377.class, args);
    }
}

```

⑤业务类。

**注意：@RefreshScope要加在controller类中才能实现动态更新，加载主启动类上不会实现动态更新，这与Consul的配置不同**

```java
package com.atguigu.cloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RefreshScope
public class NacosConfigController {

    @Value("${config.info}")
    private String configInfo;

    @GetMapping(value = "/config/info")
    public String getConfigInfo() {
        return configInfo;
    }
}
```

### 八、Spring Cloud Sentinel

 [Sentinel](https://github.com/alibaba/Sentinel) 以流量为切入点，从流量控制、熔断降级、系统负载保护等多个维度保护服务的稳定性。

Sentinel下载：https://github.com/alibaba/Sentinel/releases

启动DashBoard命令：`java -jar sentinel-dashboard-1.8.7.jar`

访问sentinel启动界面：http://localhost:8080/ (登录账号、密码都是：**sentinel**)。

步骤：

① 创建模块(cloudalibaba-provider-payment8401)。

②改pom。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>cloud24</artifactId>
        <groupId>com.atguigu.cloud</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>cloudalibaba-sentinel-service8401</artifactId>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>

        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
        </dependency>

        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>

        <dependency>
            <groupId>com.atguigu.cloud</groupId>
            <artifactId>cloud-api-commons</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <!--hutool-->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>
        <!--lombok-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.28</version>
            <scope>provided</scope>
        </dependency>
        <!--test-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>

```

③写yml。

```yaml
server:
  port: 8401

spring:
  application:
    name: cloudalibaba-sentinel-service
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
      sentinel:
        transport:
          dashboard: localhost:8080 #sentinel dashboard控制台服务地址
          port: 8719 #默认8179端口，假如被占会自动从8179开始一次+1扫描，直至找到未被占用的端口
```

④主启动。

```java
package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author QRH
 * @date 2024/4/4 13:34
 * @description TODO
 */
@SpringBootApplication
@EnableDiscoveryClient
public class Main8401 {
    public static void main(String[] args) {
        SpringApplication.run(Main8401.class, args);
    }
}

```

⑤业务类。

```java
package com.atguigu.cloud.controller;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class FlowLimitController {

    @GetMapping(value = "/testA")
    public String testA() {
        return "----testA";
    }

    @GetMapping(value = "/testB")
    public String testB() {
        return "----testB";
    }


}

```

⑥启动。

##### 8.1 流控规则

##### 8.2 @SentinelSource注解

@SentinelResource 注解用来标识资源是否被`限流`、`降级`。该注解是写在`Service层的方法上`的。

##### 8.3 热点规则

是什么？

* 经常访问的数据，很多时候我们希望监控，我们希望对经常访问的数据进行`热点参数`的监控，即对经常访问的数据进行`限流`。

```java
 @GetMapping(value = "/testHotKey")
@SentinelResource(value = "testHotKey", blockHandler = "dealHandlerTestHotKey")
public String testHotKey(@RequestParam(value = "p1", required = false)String p1,
@RequestParam(value = "p2", required = false)String p2){
        return"-------testHotKey";
        }

public String dealHandlerTestHotKey(String p1,String p2,BlockException e){
        return"------------dealHandlerTestHotKey 点击太快，限流了";
        }
```

![img_2.pgn](studyImgs/img_2.png)

##### 8.4 黑白名单控制

* 黑白名单控制，就是对请求的ip进行限制，比如只允许白名单的ip访问，或者只允许黑名单的ip访问。

需要重写RequestOriginParser.java，并设值参数名为serverName。

```java
package com.atguigu.cloud.hander;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;


@Component
public class MyRequestOriginParser implements RequestOriginParser {
    @Override
    public String parseOrigin(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getParameter("serverName");
    }
}

```

业务类：

```java
package com.atguigu.cloud;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class EmpowerController {

    @GetMapping(value = "/empower")
    public String requestSentinel() {
        log.info("测试Sentinel授权规则");
        return "Sentinel授权规则";
    }
}

```

![img_3.png](studyImgs/img_3.png)

localhost:8401/empower?serverName=test1或localhost:8401/empower?serverName=test2 的都不予通过，其他的可以访问。

##### 8.5 规则持久化

* 规则持久化，就是将规则持久化到数据库中，这样，当服务器重启的时候，这些规则就会自动加载到内存中。

步骤：

1. 引入依赖。

```xml

<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-datasource-nacos</artifactId>
</dependency>

```

2、改yml

```yml

spring:
  cloud:
    sentinel:
      datasource:
        ds1:
          nacos:
            server-addr: localhost:8848
            dataId: ${spring.application.name}
            groupId: DEFAULT_GROUP
            data-type: json
            rule-type: flow #flow:流控规则  degrade:降级规则  param-flow:参数流控规则
```

##### 8.5 Openfeign和Sentinel整合

①提供者模块（cloudalibaba-provider-payment9001）引入依赖。

```xml

<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

②提供者模块（cloudalibaba-provider-payment9001）写yml。

```yml
server:
  port: 9001


spring:
  application:
    name: nacos-payment-provider
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848 #配置nacos地址
  sentinel:
    trasport:
      dashboard: localhost:8080
      port: 8719

```

③业务类。

**PayAlibabaController.java**

```java
@GetMapping(value = "/pay/nacos/get/{orderNo}")
@SentinelResource(value = "getPayByOrder", blockHandler = "handlerBlockHandler")
public ResultData getPayByOrder(@PathVariable("orderNo")String orderNo){
    PayDTO payDTO=new PayDTO();
    payDTO.setId(1024);
    payDTO.setOrderNo(orderNo);
    payDTO.setAmount(BigDecimal.valueOf("9.91"));
    payDTO.setPayNo("pay: "+IdUtil.fastUUID());
    payDTO.setUserId(1);
    return ResultData.success("查询返回值： "+payDTO);
}

public ResultData handlerBlockHandler(@PathVariable("orderNo")String orderNo,Throwable e){
    return ResultData.fail(ReturnCodeEnum.RC500.getCode(),"getPayByOrder服务不可用，触发sentinel流控配置规则");
}
```

④公共模块（cloud-api-commons）引入依赖。

```xml

<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

⑤公共模块（cloud-api-commons）新建PayFeignSentinelApi.java。

```java
package com.atguigu.cloud.apis;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.atguigu.cloud.resp.ResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author QRH
 * @date 2024/4/5 12:41
 * @description 用OpenFeign和Sentinel的整合
 */
@FeignClient(value = "nacos-payment-provider", fallback = PayFeignSentinelApiFallback.class)
public interface PayFeignSentinelApi {

    @GetMapping(value = "/pay/nacos/get/{orderNo}")
    public ResultData getPayByOrder(@PathVariable("orderNo") String orderNo);
}

```

Sentinel回调类。

```java
package com.atguigu.cloud.apis;

import com.atguigu.cloud.resp.ResultData;
import com.atguigu.cloud.resp.ReturnCodeEnum;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author QRH
 * @date 2024/4/5 12:44
 * @description Sentinel调用失败的回调
 */
@Component
public class PayFeignSentinelApiFallback implements PayFeignSentinelApi {
    @Override
    public ResultData getPayByOrder(@PathVariable("orderNo") String orderNo) {
        return ResultData.fail(ReturnCodeEnum.RC500.getCode(), "getPayByOrder服务不可用，触发sentinel流控配置规则");
    }
}

```

⑥消费者模块（cloudalibaba-consumer-nacos-order83）引入以下依赖。

```xml

<dependency>
    <groupId>com.atguigu.cloud</groupId>
    <artifactId>cloud-api-commons</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>
```

改yml。

```yml
#激活feign对sentinel的支持
feign:
  sentinel:
    enabled: true
```

主启动类添加@EnableFeignClients注解。

业务类：

**OrderNacosController.java**

```java
@Resource
private PayFeignSentinelApi payFeignSentinelApi;

@GetMapping(value = "/consumer/pay/nacos/get/{orderNo}")
public ResultData getPayByOrder(@PathVariable("orderNo") String orderNo){
    return payFeignSentinelApi.getPayByOrder(orderNo);
}
```

测试：

启动83会报错：
![img_4.png](studyImgs/img_4.png)

导致原因：boot+cloud版本太高，alibaba的sentinel版本与cloud版本不匹配，导致报错。

解决方案： 降低父工程版本。
![img_5.png](studyImgs/img_5.png)

##### 8.6 Sentinel整合Gateway

①新建模块(cloudalibaba-sentinel-gateway9528)。

②改pom。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>cloud24</artifactId>
        <groupId>com.atguigu.cloud</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>cloudalibaba-sentinel-gateway9528</artifactId>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba.csp</groupId>
            <artifactId>sentinel-transport-simple-http</artifactId>
            <version>1.8.6</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba.csp</groupId>
            <artifactId>sentinel-spring-cloud-gateway-adapter</artifactId>
            <version>1.8.6</version>
        </dependency>
        <dependency>
            <groupId>javax.annotation</groupId>
            <artifactId>javax.annotation-api</artifactId>
            <version>1.3.2</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

③写yml。

```yml
server:
  port: 9528


spring:
  application:
    name: cloudalibaba-sentinel-service
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
    gateway:
      routes:
        - id: pay_routh1
          uri: http://localhost:9001
          predicates:
            - Path=/pay/**
```

④主启动。

```java
package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author QRH
 * @date 2024/4/5 13:37
 * @description TODO
 */
@SpringBootApplication
@EnableDiscoveryClient
public class Main9528 {
    public static void main(String[] args) {
        SpringApplication.run(Main9528.class, args);
    }
}

```

⑤业务类
**GatewayConfiguration.java**

```java
package com.atguigu.cloud.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author QRH
 * @date 2024/4/5 13:42
 * @description TODO
 */
@Configuration
public class GatewayConfiguration {
    private final List<ViewResolver> viewResolvers;

    private final ServerCodecConfigurer serverCodecConfigurer;

    public GatewayConfiguration(ObjectProvider<List<ViewResolver>> viewResolversProvider, ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
        // Register the block exception handler for Spring Cloud Gateway.
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    @Bean
    @Order(-1)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    @PostConstruct  //javax.annotation.PostConstruct
    public void doInit() {
        //自己写
        initBlockHandler();

    }

    //处理+自定义
    private void initBlockHandler() {
        Set<GatewayFlowRule> rules = new HashSet<>();
        rules.add(new GatewayFlowRule("pay_routh1").setCount(2).setIntervalSec(1));
        GatewayRuleManager.loadRules(rules);

        BlockRequestHandler handler = new BlockRequestHandler() {
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("errorCode: ", HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
                map.put("errorMsg: ", "请求太过频繁，系统忙不过来，出发限流（sentinel+gateway整合案例）");
                return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(map)
                        );

            }
        };

        GatewayCallbackManager.setBlockHandler(handler);
    }

}

```

最后测试
http://localhost:9528/pay/nacos/765

### 九、Seata分布式事务

1.Seata是什么？

* Seata是阿里巴巴开源的分布式事务解决方案，在微服务架构下，通过 Seata 提供分布式事务管理功能，使开发人员可以快速完成分布式事务的开发。

2.Seata分布式事务流程

* 1.创建全局事务
* 2.创建分支事务
* 3.提交或回滚全局事务
* 4.提交或回滚分支事务

3.Seata分布式事务流程图
![img_6.png](studyImgs/img_6.png)

下载路径：https://github.com/apache/incubator-seata/releases/tag/v2.0.0

更改seata的配置文件：(application.yml)

![img_7.png](studyImgs/img_7.png)

```yml
#  Copyright 1999-2019 Seata.io Group.

#

#  Licensed under the Apache License, Version 2.0 (the "License");

#  you may not use this file except in compliance with the License.

#  You may obtain a copy of the License at

#

#  http://www.apache.org/licenses/LICENSE-2.0

#

#  Unless required by applicable law or agreed to in writing, software

#  distributed under the License is distributed on an "AS IS" BASIS,

#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

#  See the License for the specific language governing permissions and

#  limitations under the License.



server:
  port: 7091



spring:
  application:
    name: seata-server



logging:
  config: classpath:logback-spring.xml
  file:
    path: ${log.home:${user.home}/logs/seata}
  extend:
    logstash-appender:
      destination: 127.0.0.1:4560
    kafka-appender:
      bootstrap-servers: 127.0.0.1:9092
      topic: logback_to_logstash

console:
  user:
    username: seata
    password: seata

seata:
  config:
    type: nacos
    nacos:
      server-addr: 127.0.0.1:8848
      namespace:
      group: SEATA_GROUP #后续自己在nacos里面新建,不想新建SEATA_GROUP，就写DEFAULT_GROUP
      username: nacos
      password: nacos
  registry:
    type: nacos
    nacos:
      application: seata-server
      server-addr: 127.0.0.1:8848
      group: SEATA_GROUP #后续自己在nacos里面新建,不想新建SEATA_GROUP，就写DEFAULT_GROUP
      namespace:
      cluster: default
      username: nacos
      password: nacos
  store:
    mode: db
    db:
      datasource: druid
      db-type: mysql
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/seata?characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true
      user: root
      password: root
      min-conn: 10
      max-conn: 100
      global-table: global_table
      branch-table: branch_table
      lock-table: lock_table
      distributed-lock-table: distributed_lock
      query-limit: 1000
      max-wait: 5000

  #  server:
  #    service-port: 8091 #If not configured, the default is '${server.port} + 1000'
  security:
    secretKey: SeataSecretKey0c382ef121d778043159209298fd40bf3850a017
    tokenValidityInMilliseconds: 1800000
    ignore:
      urls: /,/**/*.css,/**/*.js,/**/*.html,/**/*.map,/**/*.svg,/**/*.png,/**/*.jpeg,/**/*.ico,/api/v1/auth/login,/metadata/v1/**
```

先启动nacos服务器，再启动seata服务,最后访问seata

![img_8.png](studyImgs/img_8.png)

![img_10.png](studyImgs/img_10.png)

![img_9.png](studyImgs/img_9.png)

#### 9.1 测试用例

步骤：

1、创建三个业务数据库

```sql
create database seata_order;
create database seata_storage;
create database seata_account;

```

2、按照上述三个库分别创建undo_log回滚日志表

```sql
use seata_storage;
CREATE TABLE IF NOT EXISTS `undo_log`
(
    `branch_id`     BIGINT       NOT NULL COMMENT 'branch transaction id',
    `xid`           VARCHAR(128) NOT NULL COMMENT 'global transaction id',
    `context`       VARCHAR(128) NOT NULL COMMENT 'undo_log context,such as serialization',
    `rollback_info` LONGBLOB     NOT NULL COMMENT 'rollback info',
    `log_status`    INT(11)      NOT NULL COMMENT '0:normal status,1:defense status',
    `log_created`   DATETIME(6)  NOT NULL COMMENT 'create datetime',
    `log_modified`  DATETIME(6)  NOT NULL COMMENT 'modify datetime',
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='AT transaction mode undo table';
ALTER TABLE `undo_log`
    ADD INDEX `ix_log_created` (`log_created`);
```

3、按照上述三个表分别创建对应的表

**seata_order库 t_order表**

```sql
CREATE TABLE t_order
(
    `id`         BIGINT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id`    BIGINT(11)     DEFAULT NULL COMMENT '用户id',
    `product_id` BIGINT(11)     DEFAULT NULL COMMENT '产品id',
    `count`      INT(11)        DEFAULT NULL COMMENT '数量',
    `money`      DECIMAL(11, 0) DEFAULT NULL COMMENT '金额',
    `status`     INT(1)         DEFAULT NULL COMMENT '订单状态: 0:创建中; 1:已完结'
) ENGINE = INNODB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

SELECT *
FROM t_order;
```

**seata_account库t_account表**

```sql
CREATE TABLE t_account
(
    `id`      BIGINT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    `user_id` BIGINT(11)     DEFAULT NULL COMMENT '用户id',
    `total`   DECIMAL(10, 0) DEFAULT NULL COMMENT '总额度',
    `used`    DECIMAL(10, 0) DEFAULT NULL COMMENT '已用账户余额',
    `residue` DECIMAL(10, 0) DEFAULT '0' COMMENT '剩余可用额度'
) ENGINE = INNODB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8;

INSERT INTO t_account(`id`, `user_id`, `total`, `used`, `residue`)
VALUES ('1', '1', '1000', '0', '1000');

SELECT *
FROM t_account;
```

**seata_storage库t_storage表**

```sql
CREATE TABLE t_storage
(
    `id`         BIGINT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `product_id` BIGINT(11) DEFAULT NULL COMMENT '产品id',
    `total`      INT(11)    DEFAULT NULL COMMENT '总库存',
    `used`       INT(11)    DEFAULT NULL COMMENT '已用库存',
    `residue`    INT(11)    DEFAULT NULL COMMENT '剩余库存'
) ENGINE = INNODB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

INSERT INTO t_storage(`id`, `product_id`, `total`, `used`, `residue`)
VALUES ('1', '1', '100', '0', '100');

SELECT *
FROM t_storage;
```

4、mybatis一键生成上述三个库

5、公共模块（cloud-api-common）创建AccountFeignApi.java和StorageFeignApi.java

6、新建订单Order微服务 新建订单Storage微服务 新建订单Account微服务

7、测试

http://localhost:2004/order/create?userId=1&productId=1&count=10&money=100

出现报错，原因是spring+cloud版本太高与seata版本不兼容。

![img_11.png](studyImgs/img_11.png)

解决方案，降低spring+cloud版本

#### 9.2 @GlobalTransation注解

@GlobalTransation注解，在微服务中，我们使用@GlobalTransation注解来开启全局事务，

### 十、ElasticSearch

ElasticSearch是一个基于Lucene构建的开源的分布式搜索服务器。

Elasticsearch是一个基于Lucene的搜索服务器。它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful
web接口。Elasticsearch是用Java开发的，并作为Apache许可条款下的开放源码发布，是当前流行的企业级搜索引擎。设计用于云计算中，能够达到高可用。

#### 10.1 倒排索引

倒排索引，也叫倒排索引，是一种索引结构，用于快速查找包含特定单词的文档。

elasticsearch采用倒序排序：

* 文档（document）：每条数据就是一个文档
* 词条（term）：文档按照语义划分为不同的词条

什么是正向索引？

* 基于文档id创建索引。根据id查询快，但是查询词条时`必须先`找到文档，而`后判断`是否包含词条

什么是倒序排序？

* 对文档内容分词，对词条创建索引，并记录词条所在文档的id，查询时`先根据`词条查询到文档id，而`后根据`文档id查询文档

#### 10.2 IK分词器

#### 10.3 索引库操作

#### 10.4 DSL查询

### 十一、Redis

#### 11.1 主从集群

全量同步和增量同步的区别？

* 全量同步：master将完整内存数据生成rdb，发送rdb到slave
* 增量同步：slave提交自己的offset到master，master获取repl_baklog中slave的offset之后的命令给slave

什么时候执行全量同步？

* slave节点第一次连接master节点时
* slave结点断开时间太久，repl_baklog中的offset已经被覆盖时

什么时候执行增量同步？

* slave结点断开又恢复，并且在repl_baklog中能找到offset时

#### 11.2 哨兵原理

哨兵的作用：

* 监控：sentinel会不断检查master和slave是否按照预期工作
* 自动故障切换：如果master故障，sentinel会将一个slave提升为master。当故障实例恢复后也以新的master为主
* 通知：当集群发生故障转移时，sentinel会将最新节点角色信息推送给redis客户端

#### 11.3 分片集群

主从何哨兵可以解决高可用、高并发问题，但有两个问题依然没有解决：

* 海量数据存储问题
* 高并发读写问题

### 十二、Spring Security

#### 12.1 初始使用Spring Security6

1、引入jar

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
</dependency>
```

2、编写启动类和controller

```java

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

}
```

3、写html模板

```html

<html xmlns:th="https://www.thymeleaf.org">

<head>
    <title>Hello Security</title>
</head>

<h1>Hello Security</h1>
<a th:href="@{/logout}">退出登录</a>

</html>
```

4、写yml

```yml
server:
  port: 9090

spring:
  application:
    name: spring-security


  security:
    user:
      name: admin  #默认名字为user
      password: 123456 #不指定密码，则在启动时springsecurity会在控制台默认生成一个密码，以供登录用


```

5、启动测试

#### 12.2 Java自动配置

```java
package com.atguigu.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * @author QRH
 * @date 2024/4/29 14:13
 * @description TODO
 */
@Configuration
//@EnableWebSecurity //开启spring security的自定义配置(springboot项目可以省略这个注解，非springboot项目必须加）
public class WebSecurityConfig {
    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(
                User.withDefaultPasswordEncoder()
                        .username("user")
                        .password("123456")
                        .roles("USER")
                        .build()
        );
        return manager;
    }

}

```

#### 12.3 基于数据库的用户验证功能

1、创建DBUserDetailManager.java

```java
package com.atguigu.security.config;

import com.atguigu.security.entity.User;
import com.atguigu.security.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author QRH
 * @date 2024/4/29 15:29
 * @description TODO
 */
public class DBUserDetailManager implements UserDetailsService {
    @Resource
    private UserMapper userMapper;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().eq(User::getUsername, username);

        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        } else {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    user.getEnabled(),
                    true, //用户账号是否过期
                    true,//用户凭证是否过期
                    true,//用户是否被锁定
                    authorities//权限列表
            );
        }
    }
}

```

2、修改WebSecurityConfig.java

```java
package com.atguigu.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * @author QRH
 * @date 2024/4/29 14:13
 * @description TODO
 */
@Configuration
//@EnableWebSecurity //开启spring security的自定义配置(springboot项目可以省略这个注解，非springboot项目必须加）
public class WebSecurityConfig {
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


    //获取直接给DBUserDetailManager添加@Component注解，下面的代码就不用写了
    @Bean
    public UserDetailsService userDetailsService() {
        return new DBUserDetailManager();
    }

}

```

#### 12.4 密码加密方式

#### 12.5 前后端分离

### 十三、Java设计模式

#### 13.1 单例模式

单例模式有个特点：

* 1、无参构造器`私有化`

* 2、类中提供一个静态的`成员变量`

* 3、提供一个`静态的函数`供外部获取对象实例

eg:

```java
public class Singleton {

    private Singleton() {}

    private static Singleton instant;

    public static Singleton getInstant() {
        return instant;
    }
}
```

##### 13.1.1 饿汉式（静态常量法）

```java
package com.design.pattern.singleton;

/**
 * @author QRH
 * @date 2024/5/6 14:40
 * @description 单列模式中的饿汉式（静态常量）法
 *
 * 优点：写法简单，在类装载时就完成实例化，避免了线程同步的问题<br/>
 * 缺点：在类装载时就完成实例化，没有达到延迟加载(Lazy Load)的效果，如果这个实例自始至终没有使用，则会造成内存浪费<br/>
 *
 *
 */
public class _1HungryStaticInstant {
    public static void main(String[] args) {
        Singleton instance1 = Singleton.getInstance();
        Singleton instance2 = Singleton.getInstance();

        System.out.println(instance1 == instance2); //true

        System.out.println(instance1.hashCode());//instant1和instant2的哈希值一样
        System.out.println(instance2.hashCode());//instant1和instant2的哈希值一样
    }
}

class Singleton {

    //1、私有化无参构造器
    private Singleton() {}

    //2、将该变量用final修饰，并初始化
    private final static Singleton instant = new Singleton();

    //3、提供一个静态函数供外部获取该类的实例对象
    public static Singleton getInstance() {
        return instant;
    }

}
```

**优缺点：**

* 优点：写法简单，在`类装载时完成初始化`，避免了`线程同步`问题。

* 缺点：在类装载时就完成初始化，没有达到`懒加载（Lazy Loading)`的效果，如果自始至终都没有使用过这个实例，则会`造成内存浪费`。

* 这种基于类加载机制避免了多线程的同步问题，不过，instant在类加载时就实例化，在单例模式中大多数都是调用getInstant方法，但是导致类加载的原因又跟多种，因此不能确定有其他方式（或其他的静态方法）子类加载，这时候初始化instant就没有达到懒加载的效果。
  
* 结论：这种单例模式`可用`，但可能造成内存浪费

##### 13.1.2 饿汉式（静态代码块法）

```java
package com.design.pattern.singleton;

/**
 * @author QRH
 * @date 2024/5/6 15:09
 * @description 单例模式饿汉式（静态代码块）法
 *
 * 优点：写法简单，在类装载时就完成实例化，避免了线程同步的问题<br/>
 * 缺点：在类装载时就完成实例化，没有达到延迟加载(Lazy Load)的效果，如果这个实例自始至终没有使用，则会造成内存浪费<br/>
 *
 */
public class _2HungryStaticBlock {
    public static void main(String[] args) {
        Singleton2 instant1 = Singleton2.getInstance();
        Singleton2 instance2 = Singleton2.getInstance();

        System.out.println(instant1 == instance2); //true

        System.out.println(instant1.hashCode());//instant1和instant2的哈希值一样
        System.out.println(instance2.hashCode());//instant1和instant2的哈希值一样
    }
}

class Singleton2 {

    //1、私有化无参构造器
    private Singleton2() {}

    //2、声明静态成员变量
    private static Singleton2 instant;

    //3、静态代码块内初始化instant
    static {
        instant = new Singleton2();
    }

    //4、提供静态方法供外部获取该实例对象
    public static Singleton2 getInstance() {
        return instant;
    }
}

```

**优缺点：**

* 优点：写法简单，在`类装载时完成初始化`，避免了`线程同步`问题。

* 缺点：在类装载时就完成初始化，没有达到`懒加载（Lazy Loading)`的效果，如果自始至终都没有使用过这个实例，则会`造成内存浪费`。

* 这种方式和上面`饿汉式（静态常量法）`优缺点一样，只不过将实例化放到了静态代码块内，也就是在类装载时就执行静态代码块内的代码，初始化实例。

* 结论：这种单例模式`可用`，但可能造成内存浪费

##### 13.1.3 懒汉式（线程不安全）

```java
package com.design.pattern.singleton;

/**
 * @author QRH
 * @date 2024/5/6 15:20
 * @description 单例模式懒汉式（线程不安全）
 *
 * 优点：起到懒加载的效果，但是只能在单线程中使用。<br/>
 * 缺点：如果在多线程下，一个线程进入if(instant==null)判断语句块，还未来得及往下执行，另一个线程也通过了这个判断语句，这时便会产生多个实例。
 * 所以多线程环境下不可以使用这种方式<br/>
 *
 *实际开发中，不要使用这种方式
 */
public class _3LazyThreadUnsafe {
    public static void main(String[] args) {
        Singleton3 instance1 = Singleton3.getInstance();
        Singleton3 instance2 = Singleton3.getInstance();

        System.out.println(instance1 == instance2);//true

        System.out.println(instance1.hashCode());//instant1和instant2的哈希值一样
        System.out.println(instance2.hashCode());//instant1和instant2的哈希值一样
    }
}

class Singleton3 {
    //1、私有化无参构造器
    private Singleton3() {}

    //2、声明一个静态成员变量
    private static Singleton3 instant;

    //3、提供外部访问函数，当使用到该方法时才去实例化对象
    public static Singleton3 getInstance() {
        if (instant == null) {
            instant = new Singleton3();
        }
        return instant;
    }
}

```

**优缺点：**

* 优点：起到懒加载的效果，但是只能在`单线程`中使用。

* 缺点：如果在多线程下，一个线程进入if(instant==null)判断语句块，还未来得及往下执行，另一个线程也通过了这个判断语句，这时便会`产生多个实例`。 所以多线程环境下不可以使用这种方式。

* 结论：实际开发中，不要使用这种方式

##### 13.1.4 懒汉式（线程安全，同步方法）

```java
package com.design.pattern.singleton;

/**
 * @author QRH
 * @date 2024/5/6 15:31
 * @description 懒汉式（线程安全，同步方法）
 *
 *
 */
public class _4LazyThreadSafeAndSynchronizedMethod {
    public static void main(String[] args) {
        Singleton4 instance1 = Singleton4.getInstance();
        Singleton4 instance2 = Singleton4.getInstance();

        System.out.println(instance1 == instance2);//true

        System.out.println(instance1.hashCode());//instant1和instant2的哈希值一样
        System.out.println(instance2.hashCode());//instant1和instant2的哈希值一样

    }
}

class Singleton4 {

    //1、私有化无参构造器
    private Singleton4() {}

    //2、声明静态成员变量
    private static Singleton4 instant;

    //3、对外提供方法实例化对象，不过该方法要用synchronized修饰
    public static synchronized Singleton4 getInstance() {
        if (instant == null) {
            instant = new Singleton4();
        }
        return instant;
    }
}

```

**优缺点：**

* 优点：`解决了`线程安全问题。

* 缺点：`效率太低`，每个线程想获得实例的时候，执行getInstant方法都要进行同步。而其实这个方法`只执行一次实例化`代码就够了，后面想获得这个实例，直接return就行了。方法同步效率太低。

* 结论：实际开发中，`不推荐使用`这种方式

##### 13.1.5 懒汉式（线程安全，同步代码块）

```java
package com.design.pattern.singleton;

/**
 * @author QRH
 * @date 2024/5/6 15:40
 * @description 懒汉式（线程安全，同步代码块）
 * 优点：解决了线程安全问题。<br/>
 * 缺点：效率太低，每个线程想获得实例的时候，执行getInstant方法都要进行同步。
 *      而其实这个方法执行一次实例化代码就够了，后面想获得这个实例，直接return就行了。方法同步效率太低。<br/>
 * 结论：实际开发中，不推荐使用这种方式
 */
public class _5LazyThreadSafeAndSynchronizedBlock {
    public static void main(String[] args) {

        Singleton5 instance1 = Singleton5.getInstance();
        Singleton5 instance2 = Singleton5.getInstance();

        System.out.println(instance1 == instance2);//true

        System.out.println(instance1.hashCode());//instant1和instant2的哈希值一样
        System.out.println(instance2.hashCode());//instant1和instant2的哈希值一样
    }
}

class Singleton5 {
    //1、私有化无参构造器
    private Singleton5() {}

    //2、声明静态成员变量
    private static Singleton5 instant;

    //3、提供外部获取实例的方法，这个方法内部使用同步锁
    public static Singleton5 getInstance() {
        if (instant == null) {
            synchronized (Singleton5.class) {
                instant = new Singleton5();
            }
        }
        return instant;
    }
}
```

**优缺点：**

* 优点：`解决了`线程安全问题。

* 缺点：`效率太低`，每个线程想获得实例的时候，执行getInstant方法都要进行同步。而其实这个方法`只执行一次实例化`代码就够了，后面想获得这个实例，直接return就行了。方法同步效率太低。

* 结论：实际开发中，`不推荐使用`这种方式

##### 13.1.6 双重检查

```java
package com.design.pattern.singleton;

/**
 * @author QRH
 * @date 2024/5/6 15:46
 * @description 单例模式双重检查
 * 优点：双重检查概念是多线程开发中常使用到的，如代码所示，使用了两次if(instant==null)检查，这样可以保证线程安全。这样代码只用执行一次，后面再访问时，判断if(instant==null)，直接return实例化对象，也避免了反复进行方法同步<br/>
 * 线程安全：延迟加载，效率较高。<br/>
 * 结论：实际开发中，推荐这种方式
 */
public class _6DoubleCheck {
    public static void main(String[] args) {
        Singleton6 instance1 = Singleton6.getInstance();
        Singleton6 instance2 = Singleton6.getInstance();

        System.out.println(instance1 == instance2);//true

        System.out.println(instance1.hashCode());//instant1和instant2的哈希值一样
        System.out.println(instance2.hashCode());//instant1和instant2的哈希值一样
    }
}

class Singleton6 {
    //1、私有化无参构造器
    private Singleton6() {}

    //2、声明静态成员变量
    private static Singleton6 instant;

    //提供静态共有方法，加入双重检查代码，解决线程安全问题，同时解决懒加载问题，以及同步效率问题
    public static Singleton6 getInstance() {
        if (instant == null) {
            synchronized (Singleton6.class) {
                if (instant == null) {
                    instant = new Singleton6();
                }
            }
        }
        return instant;
    }
}
```

**优缺点：**

* 优点：双重检查概念是多线程开发中常使用到的，如代码所示，使用了两次if(instant\==null)检查，这样可以保证线程安全。 这样代码`只用执行一次`，后面再访问时，判断if(instant\==null)，直接return实例化对象，也避免了`反复`进行方法同步
* `线程安全`,`延迟加载`，`效率较高`。
* 结论：实际开发中，<span style="font-size:18px;font-weight:bolder;color:red;">推荐</span>这种方式

##### 13.1.7 静态内部类

```java
package com.design.pattern.singleton;

/**
 * @author QRH
 * @date 2024/5/6 16:00
 * @description 静态内部类
 * * 这种采用类装载机制来保证初始化实例时只有一个线程。
 * * 静态内部类方式在Singleton7类被加载时并不会立即实例化，而是需要实例化时，调用getInstant方法，才会状态SingletonInstance类，从完成Singleton的实例化。
 * * 类的静态属性只会在第一次加载类的时候，所以，jvm帮我们保证了线程的安全性，在类进行初始化时，别的线程是无法进入的。
 * * `线程安全`,`利用静态内部类实现延迟加载`，`效率高`。
 * * 结论：实际开发中，推荐这种方式
 */
public class _7StaticInnerClass {
    public static void main(String[] args) {

        Singleton7 instance1 = Singleton7.getInstance();
        Singleton7 instance2 = Singleton7.getInstance();

        System.out.println(instance1 == instance2);//true

        System.out.println(instance1.hashCode());//instant1和instant2的哈希值一样
        System.out.println(instance2.hashCode());//instant1和instant2的哈希值一样

    }
}

class Singleton7 {
    //1、私有化无参构造器
    private Singleton7() {
    }

    //2、声明静态成员变量，不过该变量要用volatile修饰
    private static volatile Singleton7 instant;

    //3、创建静态内部类
    private static class SingletonInstance {
        private final static Singleton7 INSTANT = new Singleton7();
    }

    //4、提供静态的共有方法，放回SingletonInstance.INSTANT，该方法用synchronized修饰
    public static synchronized Singleton7 getInstance() {
        return SingletonInstance.INSTANT;
    }
}
```

**优缺点：**

* 这种采用类装载机制来保证初始化实例时只有一个线程。
* 静态内部类方式在Singleton7类被加载时并不会立即实例化，而是需要实例化时，调用getInstant方法，才会状态SingletonInstance类，从完成Singleton的实例化。
* 类的静态属性只会在第一次加载类的时候，所以，jvm帮我们保证了线程的安全性，在类进行初始化时，别的线程是无法进入的。
* `线程安全`,`利用静态内部类实现延迟加载`，`效率高`。
* 结论：实际开发中，<span style="font-size:18px;font-weight:bolder;color:red;">推荐</span>这种方式。

##### 13.1.8 枚举

```java
package com.design.pattern.singleton;

/**
 * @author QRH
 * @date 2024/5/6 16:14
 * @description 单例模式-枚举
 *
 * 借助jdk1.5中添加的枚举类来实现单例模式，不仅能避免多线程同步的问题，而且还能防止反序列化重新建新的对象
 *
 * 推荐使用
 */
public class _8enum {
    public static void main(String[] args) {
        Singleton8 instant1 = Singleton8.INSTANT;
        Singleton8 instant2 = Singleton8.INSTANT;

        System.out.println(instant1 == instant2);

        System.out.println(instant1.hashCode());
        System.out.println(instant2.hashCode());

        instant1.sayOK();
    }
}

enum Singleton8 {
    INSTANT;

    public void sayOK() {
        System.out.println("ok~");
    }
}

```

**优缺点：**

* 借助jdk1.5中添加的枚举类来实现单例模式，不仅能避免多线程同步的问题，而且还能防止反序列化重新建新的对象

* 推荐使用

##### 13.1.9 注意事项和细节

* 单例模式保证了系统内存中该类只存在一个对象，节省了系统资源，对于一些需要频繁创建销毁的对象，使用单例模式可以提供啊系统性能。
* 当实例化一个单例类时，必须记住使用相应的获取对象的方法，而不是使用new。
* 单例模式的使用场景：①需要频繁进行创建和销毁的对象；②创建对象时耗时过多或耗时资源过多，但经常用到的对象； ③工具类对象；④频繁访问数据库或文件的对象（如数据源、session工厂）

#### 13.2 建造者模式

建造者模式有四个角色：

①Product（产品角色）：一个具体的产品对象

②Builder（抽象建造者）：创建一个Product对象的各个部件指定的`接口/抽象类`

③ConcreteBuilder（具体建造者）：`实现接口`，`构建和装配`各个部件

④Direct（指挥者）：`构建`一个使用Builder接口的对象，他主要是用于创建一个复杂的对象，主要有两个作用：一、`隔离`客户与对象的生产关系；二、负责`控制`产品对象的生产过程

##### 13.2.1 实现步骤

①创建一个产品角色

```java
package com.design.pattern.builder.pojo;

public class House {
    private String base;
    private String wall;
    private String roofed;

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getWall() {
        return wall;
    }

    public void setWall(String wall) {
        this.wall = wall;
    }

    public String getRoofed() {
        return roofed;
    }

    public void setRoofed(String roofed) {
        this.roofed = roofed;
    }
}

```

②创建建造者对象

```java
package com.design.pattern.builder.abstracts;

import com.design.pattern.builder.pojo.House;

public abstract class HouseBuilder {
    protected static House house = new House();

    //打地基
    public abstract void buildBase();

    //建墙
    public abstract void buildWall();

    //封顶
    public abstract void roofed();

    public House buildHouse() {
        return house;
    }
}

```

③创建具体建造者对象

```java
package com.design.pattern.builder.extend ;

import com.design.pattern.builder.abstracts.HouseBuilder;

public class CommonHouse extends HouseBuilder {
    @Override
    public void buildBase() {
        System.out.println("Common-打地基5m");
    }

    @Override
    public void buildWall() {
        System.out.println("Common-建墙10m");
    }

    @Override
    public void roofed() {
        System.out.println("Common-封顶");
    }
}

```

```java
package com.design.pattern.builder.extend ;

import com.design.pattern.builder.abstracts.HouseBuilder;

public class HighBuildingHouse extends HouseBuilder {

    @Override
    public void buildBase() {
        System.out.println("HighBuilding-打地基100m");
    }

    @Override
    public void buildWall() {
        System.out.println("HighBuilding-建墙20m");
    }

    @Override
    public void roofed() {
        System.out.println("HighBuilding-封顶");
    }
}

```

④创建指挥者对象

```java
package com.design.pattern.builder.director;

import com.design.pattern.builder.pojo.House;
import com.design.pattern.builder.abstracts.HouseBuilder;
 
public class HouseDirector {
    protected HouseBuilder houseBuilder = null;

    public HouseDirector(HouseBuilder houseBuilder) {
        this.houseBuilder = houseBuilder;
    }

    public void setHouseBuilder(HouseBuilder houseBuilder) {
        this.houseBuilder = houseBuilder;
    }

    //交给指挥者来做
    public House constructHouse() {
        houseBuilder.buildBase();
        houseBuilder.buildWall();
        houseBuilder.roofed();

        return houseBuilder.buildHouse();
    }
}

```

⑤测试

```java
package com.design.pattern.builder;

import com.design.pattern.builder.abstracts.HouseBuilder;
import com.design.pattern.builder.director.HouseDirector;
import com.design.pattern.builder.extend.CommonHouse;
import com.design.pattern.builder.extend.HighBuildingHouse;
import com.design.pattern.builder.pojo.House;
 
public class Main {
    public static void main(String[] args) {
       //件普通房子
        CommonHouse commonHouse = new CommonHouse();
        HouseDirector houseDirector = new HouseDirector(commonHouse);
        House house = houseDirector.constructHouse();

        System.out.println("==========输出过程==========");

        //盖高楼
        HighBuildingHouse highBuildingHouse = new HighBuildingHouse();
        houseDirector.setHouseBuilder(highBuildingHouse);
        House house1 = houseDirector.constructHouse();
        System.out.println("==========输出过程==========");

    }
}

```

##### 13.2.2 细节与说明

* 客户端`不必知道产品内部组成的细节`，将产品本身与产品的`创建过程解耦`，使得相同的创建过程可以创建不同的产品对象。
* 每一个具体创建者都`相对独立`，而与其他的具体建造者无关，因此可以很方便地替换具体建造者或增加新的具体建造者，用户使用不同的具体建造者即可得到不同的产品对象。
* 可以更加精细地控制产品的创建过程，将复杂产品的创建`步骤分解`在不同的方法中，使得创建过程更加清晰，也方便使用程序来控制创建过程。
* 增加新的具体建造者无需修改原有类库的代码，指挥者类针对抽象建造者类编程，系统扩展方便，符合“开闭原则”。
* 建造者模式所创建的产品一般具有`较多的共同点`，其`组成部分相似`，如果产品之间的差异性很大，则不适合使用建造者模式，因此其适用范围受到一定的`限制。`
* 如果产品的内部变化复杂，可能会导致需要定义很多具体建造者类来实现这种变化，导致系统变得庞大，因此在这种情况下，要考虑是否选择建造者模式。

抽象工厂模式和建造者模式的区别：
* 抽象工厂模式实现对`产品家族`的创建，一个产品家族是这样的一系列产品：具有不同分类维度的产品组合，采用抽象工厂模式`不需要关系构建过程`，只关心什么产品由什么工厂生产即可。<br>
而建造者模式则是要求按照`指定的蓝图`建造产品，它的主要目的是通过`组装零配件`而生产的一个新产品。

#### 13.3 代理模式

代理模式：为一个对象`提供一个替身`，以控制对这个对象的访问。即通过代理对象访问目标对象，这样做的好处是：可以在目标对象实现的基础上，`增强额外的功能操作`，即扩展目标对象的功能

被代理的对象可以是`远程对象`、`创建开销大的对象`或`需要安全控制的对象`

带模式有不同的形式，主要有三种：**静态代理**、**动态代理**（JDK代理、接口代理）和**Cglib代理**（可以在内存动态创建对象，而不需要实现接口，它属于动态代理的范畴）

##### 13.3.1 静态代理

静态代理在使用时，需要定义`接口或父类`，被代理对象（即目标对象）与代理对象一起`实现相同的接口`或者`继承相同父类`

①创建接口

```java
package com.design.pattern.proxy.statics;

/**
 * @author QRH
 * @date 2024/5/7 16:48
 * @description 接口，目标对象和代理对象都要实现这个接口
 */
public interface ITeacherDao {
    //授课方法
   public void teach();
}

```

②创建被代理对象（目标对象），同时实现这个接口

```java
package com.design.pattern.proxy.statics;

/**
 * @author QRH
 * @date 2024/5/7 16:50
 * @description 被代理对象，即目标对象
 */
public class TeacherDao implements ITeacherDao{
    @Override
    public void teach() {
        System.out.println("================老师授课中。。。。。");
    }
}

```

③创建代理对象，同时实现接口

```java
package com.design.pattern.proxy.statics;

/**
 * @author QRH
 * @date 2024/5/7 16:49
 * @description 代理对象
 */
public class TeacherDaoProxy implements ITeacherDao {

    private ITeacherDao target; //目标对象，通过接口来聚合

    public TeacherDaoProxy(ITeacherDao iTeacherDao) {
        this.target = iTeacherDao;
    }

    @Override
    public void teach() {
        System.out.println("开始代理，完成某些操作。。。。。");
        this.target.teach();
        System.out.println("提交。。。。");
    }
}

```

④测试

```java
package com.design.pattern.proxy.statics;

import com.design.pattern.proxy.statics.TeacherDao;
import com.design.pattern.proxy.statics.TeacherDaoProxy;

/**
 * @author QRH
 * @date 2024/5/7 16:47
 * @description TODO
 */
public class Main {
    public static void main(String[] args) {
        //创建目标对象
        TeacherDao teacherDao = new TeacherDao();

        //创建代理对象，同时将目标对象交给代理对象
        TeacherDaoProxy proxy = new TeacherDaoProxy(teacherDao);

        //执行的是被代理对象的方法
        proxy.teach();
    }
}

```

**优缺点：**

* 优点：在不修改目标对象的功能前提下，能通过代理对象对目标功能扩展。
* 缺点：因为代理对象需要与目标对象实现一样的接口，所以会有很多代理类。
* 一旦接口增加方法，目标对象与代理对象都要维护。

##### 13.3.2 动态代理

动态代理也叫jdk代理或接口代理。动态代理`不需要实现接口`，但`目标对象需要实现接口`，否者不能动态代理。

代理对象的生成，是利用JDK的API，动态的在内存中构建代理对象。

①创建接口

```java
package com.design.pattern.proxy.dynamic;

/**
 * @author QRH
 * @date 2024/5/7 17:10
 * @description 接口
 */
public interface ITeacherDao2 {
    void teach();
    void sayHello(String name);
}

```

②创建目标对象，同时实现接口

```java
package com.design.pattern.proxy.dynamic;

/**
 * @author QRH
 * @date 2024/5/7 17:12
 * @description 目标对象
 */
public class TeacherDao2 implements ITeacherDao2{
    @Override
    public void teach() {
        System.out.println("------老师上课中。。。。");
    }

    @Override
    public void sayHello(String name) {
        System.out.println("hello， "+name);
    }
}

```

③创建代理对象

```java
package com.design.pattern.proxy.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * @author QRH
 * @date 2024/5/7 17:13
 * @description 代理对象
 */
public class ProxyFactory {

    //目标对象
    private Object target;

    public ProxyFactory(Object target) {
        this.target = target;
    }

    /**
     * 给目标对象生成一个代理对象
     *
     * public static Object Proxy.newProxyInstance(ClassLoader loader,
     *                                              Class<?>[] interfaces,
     *                                              InvocationHandler h)
     *
     *   ClassLoader loader:指定当前目标对象使用的类加载器，获取加载器的方法固定
     *   Class<?>[] interfaces:目标对象实现的接口类型，使用泛型方法确认类型
     *   InvocationHandler h：事情处理，执行目标对象的方法时，会触发事情处理器方法，会把当前的目标对象方法作为参数传入
     * @return
     */
    public Object getProxyInstance() {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("JDK代理开始~~");
                        //反射机制调用目标对象的方法
                        Object invoke = method.invoke(target, args);
                        System.out.println("JDK代理提交。。");
                        return invoke;
                    }
                }
        );
    }
}

```

④测试

```java
package com.design.pattern.proxy.dynamic;

public class Main2 {
    public static void main(String[] args) {
        //声明目标对象
        ITeacherDao2 teacherDao2 = new TeacherDao2();

        //把目标对象交给代理对象,获得代理实例
        ITeacherDao2 proxyInstance =(ITeacherDao2) new ProxyFactory(teacherDao2).getProxyInstance();

//        System.out.println(proxyInstance.getClass());

        proxyInstance.teach();

        proxyInstance.sayHello("zs");
    }
}

```

### 十四、RabbitMQ

#### 14.1 RabbitMQ结构
![img_14.png](studyImgs/img_14.png)

#### 14.2 RabbitMQ安装
* Windows安装：

需要下载erlong，再安装rabbitmq，启动时需要点击`RabbitMQ Service-start.bat`开启cmd窗口，窗口不能关闭。

然后输入网址:localhost:15672进入后台。

![img_15.png](studyImgs/img_15.png)

![img_16.png](studyImgs/img_16.png)

![img_17.png](studyImgs/img_17.png)


* Docker安装：

```shell
#拉取镜像
docker pull rabbitm1:3.13-management

#-d: 后台运行docker容器
#--name：设置容器名称
#-p：映射端口号，格式：“宿主机端口号：容器内端口号”。5672供客户端程序访问，15672供后台管理界面访问
#-v：卷映射目录
#-e：设置容器内环境变量，这里设置了登录rabbit管理后台默认用户和密码

docker run -d \
--name rabbitmq \
-p 5672:5672 \
-p 15672:15672 \
-v rabbitmq-plugin:/plugins \
-e RABBITMQ_DEFAULT_USER=guest \
-e RABBITMQ_DEFAULT_PASS=123456 \
rabbitmq:3.13-management

```

#### 14.3 RabbitMQ通信模式种类

![img_18.jpg](studyImgs/img_18.jpg)

#### 14.4 SpringBoot整合RabbitMQ

基本思路：

>搭建环境
>
>基础设定：交换机名称、队列名称、绑定关系
>
>发送消息：使用RestTemplate
>
>接收消息：使用@RabbitListener注解

**消费者模块**

1、创建消费者模块（atguigu-consumer）和生产者模块（atguigu-provider），并引入依赖

```xml
  <dependencies>
      
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
  </dependency>
  <dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
  </dependency>
  <dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
  </dependency>

</dependencies>

```

2、配置文件（消费者模块和生产者模块配置都相同）

```yml
server:
  port: 8085 #消费者模块和生产者模块端口号要不相同才行
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    virtual-host: /

```

3、消费者模块创建一个类
```java
package com.atguigu.mq.com.atguigu.mq.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author QRH
 * @date 2024/5/21 11:54
 * @description TODO
 */
@Component
public class MyMessageListener {
  public static final String EXCHANGE_DIRECT="exchange.direct.order";
  public static final String ROUTING_KEY="order";
  public static final String QUEUE_NAME="queue.order";

  @RabbitListener(
          bindings = {
                  @QueueBinding(
                          value = @Queue(value = QUEUE_NAME),
                          exchange = @Exchange(value = EXCHANGE_DIRECT),
                          key = {ROUTING_KEY}
                  )
          }
  )
  public void processMessage(String dataString, Message message, Channel channel){
    System.out.println(dataString);
    System.out.println(message.toString());
    System.out.println(channel.toString());
  }
}

```

4、生产者模块创建测试类，测试发送消息
```java
package com.atguigu.mq;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author QRH
 * @date 2024/5/21 13:53
 * @description TODO
 */
@SpringBootTest
public class RabbitMQTest {
    public static final String EXCHANGE_DIRECT="exchange.direct.order";
    public static final String ROUTING_KEY="order";
    public static final String QUEUE_NAME="queue.order";

    @Resource
    private RabbitTemplate rabbitTemplate;



    @Test
    public void testSendMessage() {
        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT, ROUTING_KEY, "hello,rabbitmq atguigu");
    }



}

```

#### 14.5 消费确认机制

**新建一个生产者模块（atguigu-confirm-producer）**

配置yml
```yml
server:
  port: 8087

spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    virtual-host: /
    publisher-confirm-type: correlated #交换机确认
    publisher-returns: true #队列确认
```

创建配置类
```java
package com.atuigu.mq.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

/**
 * @author QRH
 * @date 2024/5/21 14:07
 * @description TODO
 */
@Configuration
@Slf4j
public class RabbitConfig implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback {

    @Resource
    private RabbitTemplate rabbitTemplate;
    
    @PostConstruct
    public void initRabbitTemplate() {
        //设置确认回调
        rabbitTemplate.setConfirmCallback(this);
        //设置失败回调
        rabbitTemplate.setReturnsCallback(this);
    }
    
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        //消息发送到交换机成功或失败时调用这个方法
        log.info("confirm() 回调函数打印correlationData： "+correlationData);
        log.info("confirm() 回调函数打印ack： "+ack);
        log.info("confirm() 回调函数打印cause： "+cause);
    }

    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        //发送到队列失败才调用的方法
        log.info("returnedMessage() 回调函数打印消息主体： "+new String(returnedMessage.getMessage().getBody()));
        log.info("returnedMessage() 回调函数打印应答码： "+returnedMessage.getReplyCode());
        log.info("returnedMessage() 回调函数打印描述： "+returnedMessage.getReplyText());
        log.info("returnedMessage() 回调函数打印消息使用的交换机： "+returnedMessage.getExchange());
        log.info("returnedMessage() 回调函数打印消息使用的路由键： "+returnedMessage.getRoutingKey());
    }
}

```

**新建一个消费者模块（atguigu-confirm-consumer）**

```yml

server:
  port: 8088
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    virtual-host: /
    listener:
      simple:
        acknowledge-mode: manual #把消息设置为手动确认
```

```java
package com.atguigu.mq.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author QRH
 * @date 2024/5/21 14:37
 * @description TODO
 */
@Component
@Slf4j
public class MyMessageListener {

    @RabbitListener(queues = {"queue.order"})
    public void processMessage(String dataString, Message message, Channel channel) {
        //获取当前消息的deliverTag
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("消费端 消息内容： " + dataString);
            //操作成功，返回ack
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            //获取消息是否是重复投递的
            Boolean redelivered = message.getMessageProperties().getRedelivered();
            
            //操作失败，返回nack信息
            //requeue 控制消息是否重新放回队列
            try {
                if (redelivered) {
                    //重复投递，说明已经重试过一次了，不需要重新投递
                    channel.basicNack(deliveryTag, false, false);
                } else {
                    channel.basicNack(deliveryTag, false, true);
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } finally {
        }
    }
}

```

##### 14.6 死信和死信队列

* 死信：当一个消费无法被消费，他就成了死信。

死信产生的原因大致有三种：<br>
1、拒绝：消费者拒接消息，basicNack()/basicReject()，并不会把消息重新放入原目标队列，即requeue=false。<br>
2、溢出：队列中消息数量到达限制。比如队列最大只能存储10条数据，且现在已经存储了10条，此时再发送一条消息进来，根据先进先出原则，队列中最早的消息会变成死信。<br>
3、超时：消息到达超时时间未被消费。

死信的处理方式大致有三种：<br>
1、丢弃：对不重要的消息直接丢弃，不做处理。<br>
2、入库：把死信写入数据库，日后再处理。<br>
3、监听：消息变成死信后进入死信队列，我们专门设置消费端监听死信队列，做后续处理（通常采用）。

##### 14.7 延迟队列

实现方案：<br>
1、<br>
2、插件



### 十五、JUC并发编程

> java.util.concurrent.*

#### 15.1 并发与并行

并发：`同一个实体上的多个事件`，是在`同一台处理器`上“同时”处理多个任务，同一时刻，其实只有一个事件在发生。

并行：`在不同实体上的多个事件`，是在`多台处理器`上同时处理多个任务，同一时刻，大家真的都在做事情，你做你的。

![img_26.jpg](studyImgs/img_26.jpg)

##### 15.1.1 CompletableFuture

Future接口（FutureTask实现类）定义了操作`异步线程`执行一些方法，如获取一步任务的执行结果、取消任务的执行、判断任务是否被取消、判断任务执行是否完毕等。

一句话，Future可以为`主线程`开启一个分支任务，专门处理一些耗时的操作，比如网络请求、数据库查询、文件读取等。

FutureTask的优缺点：

缺点：

1、get()阻塞。一旦调用get()方法求结果，如果计算没有完成容易导致程序阻塞。

2、isDone()轮询，轮询方式会耗费无谓的CPU资源，而且也不见得能及时得到计算结果。如果想要一步获取结果，通常会以轮询的方式去获取结果，尽量不阻塞。

优点：
。。。。




* CompletableFuture为什么出现？

CompletableFuture提供了一种与观察者模式类似的机制，通过回调函数来处理异步任务的结果。

**CompletableFuture是Future的增强版，减少阻塞和轮询，可以传入回调对象，当异步任务完成或发生异常时，自动调用回调对象的回调方法 **

使用CompletableFuture不建议使用`new`创建对象，而是使用`静态方法`来创建。

如：

`需要`返回值的，使用`supplyAsync()`方法:

> public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier);
> public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor); 

`不需要`返回值的，使用`runAsync()`方法:

> public static CompletableFuture<Void> runAsync(Runnable runnable);
> public static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor);

上述Executor executor参数说明：如果没有指定Executor的方法，直接使用默认的ForkJoinPool.commonPool()作为它的线程池执行异步代码；
如果指定线程池，则使用我们自定义的或者特别指定的线程池执行异步代码。

```java
package com.future;

import java.util.concurrent.*;

/**
 * @author QRH
 * @date 2024/5/18 18:32
 * @description TODO
 */
public class CompletableFutureBuilderDemo {

    public static void main(String[] args) throws Exception {

        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        /**
         //无返回值的
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println(completableFuture.get());

        System.out.println("--------------------------------");

        CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName());
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },threadPool);
        System.out.println(completableFuture2.get());
        **/


        //有返回值的
        CompletableFuture<String> supplyAsync = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName());
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello supplyAsync";
        });
        System.out.println(supplyAsync.get());

        System.out.println("---------------------------");

        CompletableFuture<String> supplyAsync2 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName());
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return   "Hello supplyAsync 我有线程池";
        }, threadPool);
        System.out.println(supplyAsync2.get());


        threadPool.shutdown();
    }
}

```

CompleteableFuture的优点：

1、异步任务结束时，会自动回调某个对象的方法。

2、主线程设置好回调后，不再关心一步任务的执行，异步任务之间可以顺序执行。

3、异步任务出错时，会自动调用某个对象的方法。

```java
package com.future;

import java.util.concurrent.*;

/**
 * @author QRH
 * @date 2024/5/18 18:32
 * @description TODO
 */
public class CompletableFutureUseDemo {

    public static void main(String[] args) throws Exception {

        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        try {
            CompletableFuture.supplyAsync(()->{
                System.out.println(Thread.currentThread().getName());
                int res = ThreadLocalRandom.current().nextInt(10);
                try{TimeUnit.MILLISECONDS.sleep(1);}catch (Exception e){e.printStackTrace();}
                System.out.println("----1秒后出结果： "+res);
                return res>5 ? res/0 : res;
            },threadPool).whenComplete((v,e)->{
               if(e==null){
                   System.out.println("----计算完成，更新系统value= "+v);
               }
            }).exceptionally(e->{
                e.printStackTrace();
                System.out.println("异常情况： "+e.getCause()+"\t"+e.getMessage());
                return null;
            });

            System.out.println(Thread.currentThread().getName() + "现成先去忙其他任务");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }

        //主线程不要立刻结束，否则CompletableFuture默认使用的线程会立刻关闭，这里设置暂停3秒
        //try{TimeUnit.MILLISECONDS.sleep(5);}catch (Exception e){e.printStackTrace();}
    }
}

```

##### 15.1.2 CompletableFuture的的api

CompletableFuture的的api可以划分为五种：

**1、获得结果和触发计算**

>* get()：同步获取结果，如果任务完成，则获取其结果；如果任务异常完成，则抛出异常。
>* get(long timeout, TimeUnit unit)：同步获取结果，如果任务完成，则获取其结果；如果任务异常完成，则抛出异常；如果超过指定的时间仍然未完成，则抛出超时异常。
>* join()：同步获取结果，如果任务完成，则获取其结果；如果任务异常完成，则抛出异常。
>* getNow(T valueIfAbsent)：同步获取结果，如果任务完成，则返回计算结果；如果任务异常完成，则抛出异常；如果任务尚未完成，则返回指定的默认值valueIfAbsent。
>* complete(T value)：将任务状态设置为完成，并返回结果。

```java
public static void main(String[] args) {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "abc";
        });

        /**
        //getNow("默认值")如果没计算完成，则返回给定的默认值，否则则返回计算结果
        System.out.println(completableFuture.getNow("xxx"));
        try { TimeUnit.MILLISECONDS.sleep(3); } catch (InterruptedException e) {  e.printStackTrace();  }
        System.out.println(completableFuture.getNow("xxx"));
        **/

        /**
        //complete("打断值"):如果打断计算，则把默认值给打断方法，否则不打断，返回计算结果
         
//        try { TimeUnit.MILLISECONDS.sleep(1); } catch (InterruptedException e) {  e.printStackTrace();  }
//        System.out.println(completableFuture.complete("打断值")+"\t"+completableFuture.join());

        try { TimeUnit.MILLISECONDS.sleep(2); } catch (InterruptedException e) {  e.printStackTrace();  }
        System.out.println(completableFuture.complete("打断值")+"\t"+completableFuture.join());
         
        **/
    }

```

**2、对计算结果进行处理**

>* thenApply(Function fn)：当任务完成时，执行fn，并把结果作为参数传递给fn。
>* handle(BiFunction fn)：当任务完成时，执行fn，并把结果作为参数传递给fn。

注：

* thenApply()：由于存在依赖关系（当前步错，不走下一步），当前步骤有异常的话就叫停
* handle():有异常也可以往下一步走，根据带的异常参数可以进一步处理

```java
package com.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author QRH
 * @date 2024/5/19 12:35
 * @description TODO
 */
public class CompletableFutureApi2Demo {
    public static void main(String[] args) {

        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        /**
        try {
            CompletableFuture.supplyAsync(() -> {
                        try {  TimeUnit.MILLISECONDS.sleep(1); } catch (InterruptedException e) {  e.printStackTrace(); }
                        return 1;
                    }
                    , threadPool)
                    .thenApply(f -> {
                        System.out.println("2222");
                        return f + 2;
                    })
                    .thenApply(f -> {
                        System.out.println("3333");
                        return f + 2;
                    })
                    .whenComplete((v, e) -> {
                        if (e == null) {  System.out.println("---计算结果： " + v);  }
                    })
                    .exceptionally(e -> {
                        System.out.println("----计算异常： " + e.getMessage());
                        return null;
                    });
            System.out.println(Thread.currentThread().getName() + "----主线程先去忙其他任务");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
        **/

        try {
            CompletableFuture.supplyAsync(() -> {
                        try {  TimeUnit.MILLISECONDS.sleep(1); } catch (InterruptedException e) {  e.printStackTrace(); }
                        System.out.println("111");
                        return 1;
                    }
                    , threadPool)
                    .handle((f,e) -> {
                        int i = 10 / 0;
                        System.out.println("2222");
                        return f + 2;
                    })
                    .handle((f,e) -> {
                        System.out.println("3333");
                        return f + 2;
                    })
                    .whenComplete((v, e) -> {
                        if (e == null) {  System.out.println("---计算结果： " + v);  }
                    })
                    .exceptionally(e -> {
                        System.out.println("----计算异常： " + e.getMessage());
                        return null;
                    });
            System.out.println(Thread.currentThread().getName() + "----主线程先去忙其他任务");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }

    }
}

```

**3、对计算结果进行消费**

>* thenAccept(Consumer action)：接受任务的处理结果，并进行消费，无返回结果。

执行顺序：
**thenRun() > thenAccept() > thenApply() **

* thenRun(Runnnable runnable):任务A执行完执行任务B，并且`B不需要A的结果`
* thenAccept(Consumer action):任务A执行完执行任务B，B需要A的结果，但是任务`B无返回值`
* thenApply(Function fn):任务A执行完执行任务B，B需要A的结果，同时任务`B有返回值`

```java
package com.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author QRH
 * @date 2024/5/19 12:35
 * @description TODO
 */
public class CompletableFutureApi3Demo {
  public static void main(String[] args) {

    /**

     CompletableFuture.supplyAsync(()->{
     return 1;
     })
     .thenAccept(r->{
     System.out.println(r);
     });

     System.out.println(Thread.currentThread().getName()+"--------main线程忙其他业务");
     **/


    CompletableFuture.supplyAsync(() -> "Hello Accept").thenRun(() -> {
    }).join();

    CompletableFuture.supplyAsync(() -> "Hello A2").thenAccept(System.out::println).join();

    System.out.println(CompletableFuture.supplyAsync(() -> "Hello A3").thenApply(r -> r + "  B").join());

  }
}

```

**thenRun()和thenRunAsync()区别**
> 1、没有传入自定义线程池，都是用默认的ForkJoinPool.commonPool()线程池。
> 
> 2、传入了一个自定义线程池：
> 
> 如果执行第一个任务的时候，传入了一个自定义线程池：
> 
> 调用thenRun()方法执行第二个任务时，则第二个任务和第一个任务共用同一个线程池。
> 
> 调用thenRunAsync()执行第二个任务时，则第一个任务使用的是自己传入的线程池，第二个任务使用的是ForkJoin线程池
> 
> 
> 注：有可能处理太快，系统优化切换原则，直接使用main线程处理

```java
package com.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author QRH
 * @date 2024/5/19 12:35
 * @description TODO
 */
public class CompletableFutureApi4Demo {
    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(5);

        /**
        //没有使用自定义线程池
        try {
            CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> {
                try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                System.out.println("任务1 " + Thread.currentThread().getName());
                return 1;
            })
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务2 " + Thread.currentThread().getName());
                    })
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务3 " + Thread.currentThread().getName());
                    })
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务4 " + Thread.currentThread().getName());
                    });

            System.out.println(cf.get(2L, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
        **/

        /**
        //使用自定义线程池
        try {
            CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> {
                try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                System.out.println("任务1 " + Thread.currentThread().getName());
                return 1;
            },threadPool)
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务2 " + Thread.currentThread().getName());
                    })
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务3 " + Thread.currentThread().getName());
                    })
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务4 " + Thread.currentThread().getName());
                    });

            System.out.println(cf.get(2L, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
        **/


        /**
        //第一个任务使用自定义线程池，第2个任务使用thenRunAsync()且不自定义线程池
        try {
            CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> {
                try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                System.out.println("任务1 " + Thread.currentThread().getName());
                return 1;
            },threadPool)
                    .thenRunAsync(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务2 " + Thread.currentThread().getName());
                    })
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务3 " + Thread.currentThread().getName());
                    })
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务4 " + Thread.currentThread().getName());
                    });

            System.out.println(cf.get(2L, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
        **/


        /**
        //使用thenRunAsync（）并使用自定义线程池
        try {
            CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> {
                try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                System.out.println("任务1 " + Thread.currentThread().getName());
                return 1;
            })
                    .thenRunAsync(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务2 " + Thread.currentThread().getName());
                    },threadPool)
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务3 " + Thread.currentThread().getName());
                    })
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务4 " + Thread.currentThread().getName());
                    });

            System.out.println(cf.get(2L, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
         **/

        try {
            CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> {
                //try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                System.out.println("任务1 " + Thread.currentThread().getName());
                return 1;
            },threadPool)
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(1);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务2 " + Thread.currentThread().getName());
                    })
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(1);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务3 " + Thread.currentThread().getName());
                    })
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(1);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务4 " + Thread.currentThread().getName());
                    });

            System.out.println(cf.get(2L, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }



}

```

**5、对两个结果进行合并**

* thenCombine():两个任务都完成之后，最终吧两个任务的结果一起交给thenCombine（）处理。先完成的的先等着，等待其他分支任务完成。

```java
package com.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author QRH
 * @date 2024/5/19 12:35
 * @description TODO
 */
public class CompletableFutureApi5Demo {
    public static void main(String[] args) {
        CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "\t--启动");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 10;
        });

        CompletableFuture<Integer> cf2= CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "\t--启动");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 30;
        });


        CompletableFuture<Integer> res = cf1.thenCombine(cf2, (r1, r2) -> {
            System.out.println("----开始两个结果合并");
            return r1 + r2;
        });

        System.out.println(res.join());


    }


}

```

#### 15.2 锁

##### 15.2.1 乐观锁和悲观锁

**悲观锁**：认为自己在使用数据的时候一定有别的线程来修改数据，因此在获取数据的时候会先加锁，确保数据不会被别的线程修改。<br>
`synchronized关键字`和`Lock类`的实现都是悲观锁。<br>
适合`写操作`多的场景，先加锁可以保证写操作时数据正确。显示的锁锁定之后再操作同步资源。

**乐观锁**：认为自己在使用数据的时候一定没有别的线程来修改数据，因此不会加锁。<br>
在Java中通过使用`无锁编程`来实现，只是在更新数据的时候去判断，之前有没有别的线程更新了这个数据。<br>
如果这个数据没有被更新，当前线程将自己修改的数据成功写入。<br>
如果这个数据已经被其他线程更新，则根据不同的实现方式执行不同的操作，比如放弃修改、重试抢锁等。<br>
适合`读操作`多的场景，不加锁的特点能够使其读操作的性能大幅提升。乐观锁则直接去操作同步资源，是一种无锁算法，得之我幸不得我命，再努力就是。

判断规则：<br>
1、版本号机制Version。<br>
2、最常采用的是CAS算法，Java原子类中的递增操作就通过CAS自旋实现的。

##### 15.2.2 公平锁和非公平锁

**公平锁**：指多个线程按照申请锁的顺序来获取锁，这里类似排队买票，先来的人先买后来的人在队尾排队，这是公平的。

> Lock lock=new ReentrantLock(true); //true表示公平锁，先来先得

**非公平锁**：指多个线程获取锁的顺序并不是按照申请锁的顺序，有可能后申请的线程比先申请的线程先获取锁，在高并发环境下有可能造成`优先级翻转`或者`饥饿现象`（某个线程一直得不到锁）。
> Lock lock=new ReentrantLock(false); //false表示非公平锁，后来的也可能先获得锁
> Lock lock=new ReentrantLock(); //默认非公平锁

* 为什么会有公平锁盒非公平锁的设计？为什么默认非公平？
  ①恢复挂起的线程到真正锁的获取还是有时间差的，从开发人员来看这个时间微乎其微，但是从CPU的角度来看，这个时间差存在的还是很明显的，所以非公平锁能更充分的利用CPU的时间片，尽量减少CPU空闲状态时间。

  ②使用多线程很重要的考量点是线程切换的开销，当采用非公平锁时，当一个线程请求锁获取同步状态，然后释放同步状态，所以刚释放锁的线程会在此刻再次获取同步状态的概率变得非常大，所以减少了线程的开销。

```java
package com.lock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author QRH
 * @date 2024/5/20 15:24
 * @description TODO
 */
public class SaleTicketDemo {
    public static void main(String[] args) {

        Ticket ticket = new Ticket();

        new Thread(()->{for(int i=0;i<55;i++)ticket.sale();},"a").start();

        new Thread(()->{for(int i=0;i<55;i++)ticket.sale();},"b").start();

        new Thread(()->{for(int i=0;i<55;i++)ticket.sale();},"c").start();
    }
}

class Ticket {
    private int number = 50;
    //无参就是非公平锁，有参是公平锁
    private ReentrantLock lock = new ReentrantLock(true);

    public void sale() {
        lock.lock();
        try {
            if (number > 0) {
                System.out.println(Thread.currentThread().getName() + "卖出第:  " + (number--) + "票，剩余" + number);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}

```

##### 15.2.3 可重入锁（递归锁）

可重入锁是值统一线程在外层方法获取锁的时候，再进入该线程的内层方法会自动获取锁（前提，锁对象得是同一对象），不会因为之前已经获取过还没是防而阻塞。

隐式锁：synchronized<br>
显示锁：Lock

##### 15.2.4 死锁

死锁是指两个或两个以上的进程在执行过程中，因争夺资源而造成的一种互相等待的现象，若无外力干涉那它们将无法推进下去，如果系统资源充足，进程的资源请求都能够得到满足，则系统会 deadlock（死锁）发生，即所有进程均被阻塞，这种状态称为死锁。

产生死锁的主要原因：<br>
* 系统资源不足
* 进程运行推进的顺序不合适
* 资源分配不当

排查死锁：
* 纯命令：jps -l 找到进程号，然后jstack 进程号
* 图形化工具：jconsole

#### 15.3 LockSupport和线程中断

LockSupport是jdk1.5之后出现的一个工具类，它提供了线程的阻塞和唤醒操作。<br>

##### 15.3.1  线程中断

* 什么是中断机制？
首先一个线程不应该由其他线程来强制中断或停止，而是应该由`线程自己自行停止`，自己来决定自己的命运。所以，Thread.stop,Thread.suspend.Thread.resume都已经废弃了<br>
其次，在Java中没有办法立即停止一条线程，然而停止线程却显得尤为重要，如取消一个耗时操作。因此Java提供了一种用于停止线程的`协商机制`--中断，即中断标识协商机制。
  <br>

中断只是一种协商机制，Java没有给中断增加任何语法，中断过程完全需要==程序员自己实现==。

如要中断一个线程，需要手动调用线程的interrupt方法，该方法也仅仅是将线程对象的中断标识设置为true；<br>
接着需要自己写代码不断检测当前线程的标识位，如果为true，表示当前线程请求这条线程中断。<br>
此时，究竟要做什么需要自己写代码实现。

**中断三个api**：

> public void interrupt() 只是给线程打上中断标记，发起一个协商而不会立即停止线程。
> 
> public static void interrupted() 检测当前线程是否被中断,并清除当前中断状态。<br>
> 这个方法做了两件事：<br>
> 1、返回当前线程的中断状态，测试当前线程是否已被中断。<br>
> 2、将当前的中断状态清零并重新设为true，清除当前线程的中断状态。
> 
> public boolean isInterrupted() 检测线程是否被中断，但不清除当前中断状态。


##### 15.3.2 LockSupport

LockSupport用来创建锁和其他同步类的基本线程阻塞原语。

* LockSupport.park()：让当前线程阻塞，直到被unpark()唤醒。<br>
* LockSupport.unpark(Thread thread)：唤醒一个线程。

**使用Object类中的wait和notify方法实现线程等待和唤醒**

异常情况：<br>
1、wait和notify方法，两个都去掉同步代码块，就会抛出IllegalMonitorStateException异常。<br>
2、将notify放在wait方法前面，程序会一直运行，线程无法被唤醒<br>

总结：<br>
wait和notify方法，两个都放在同步代码块中，且成对出现使用。<br>
先wait后notify才可以。

```java
private static void syncAwaitNotify() throws InterruptedException {
        Object o = new Object();
        new Thread(() -> {
//            try {
//                TimeUnit.MILLISECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            synchronized (o) {
                System.out.println(Thread.currentThread().getName() + "\t ---come in");
                try {
                    o.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "\t ---被唤醒");
            }
        }, "t1").start();

        TimeUnit.MILLISECONDS.sleep(1);

        new Thread(() -> {
            synchronized (o) {
                o.notify();
                System.out.println(Thread.currentThread().getName() + "\t ---发出通知");
            }
        }, "t2").start();
    }
```

**Condition类中的await和signal方法实现线程等待和唤醒**

异常情况：<br>
1、去掉lock/unlock，就会抛出IllegalMonitorStateException异常。<br>
2、先signal再await，程序会一直运行，线程无法被唤醒<br>

总结：<br>
Condition中的线程等待和唤醒方法，需先获得锁<br>
一定要先await后signal。

```java
 private static void conditionLock() {
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        new Thread(() -> {
//            try {
//                TimeUnit.MILLISECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "\t ---come in");
                condition.await();
                System.out.println(Thread.currentThread().getName() + "\t ---被唤醒");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

        }, "t1").start();

//        TimeUnit.MILLISECONDS.sleep(1);

        new Thread(() -> {
            lock.lock();
            try {
                condition.signal();
                System.out.println(Thread.currentThread().getName() + "\t ---发出通知");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

        }, "t2").start();
    }

```

**LockSupport类中的park和unpark方法实现线程等待和唤醒**

之前错误的先唤醒后等待，LockSupport照样支持。

* 为什么可以突破wait/notify的原有调用原则？
因为unpack获得了一个凭证，之后再调用park方法，就可以名正言顺的凭证消费，故不会阻塞。先发放了凭证后续可以畅通无阻。
  
* 为什么唤醒两次后阻塞两次，但最终结果还会阻塞线程？
因为凭证的数量`最多为1`，连续调用两次unpark和调用一次unpark效果一样，只会增加一个凭证，调用两次park却需要消费两个凭证，证不够，不能放行。

```java
public static void main(String[] args) throws Exception {

        Thread t1=new Thread(()->{
//            try {
//                TimeUnit.MILLISECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            System.out.println(Thread.currentThread().getName() + "\t --come in");
            LockSupport.park();
            System.out.println(Thread.currentThread().getName() + "\t ---被唤醒");
        },"t1");
        t1.start();

        TimeUnit.MILLISECONDS.sleep(1);

        new Thread(()->{
            LockSupport.unpark(t1);
            System.out.println(Thread.currentThread().getName() + "\t ---发出通知");
        },"t2").start();

    }
```

#### 15.4 Java内存模型JMM

JMM（Java内存模型）是JDK1.5之后出现的概念，是JVM与Java程序之间进行交互的桥梁。<br>

JMM(Java Memory Model，简称JMM)本身是一种抽象的概念并不真实存在它仅仅描述的是一组约定或规范，通过这组规范定义了程序中各个`变量的读写访问方式`并决定一个线程对`共享变量的写入何时`以及如何变成`对另一个线程可见`，关键技术点都是围绕<span style="color:red;">多线程的原子性、可见性和有序性</span>展开的。

原则：<br>
JMM的关键技术点都是围绕多线程的原子性、可见性和有序性展开的。

能干嘛？<br>
1、通过JMM来实现线程和主内存之间的抽象关系。<br>
2、屏蔽各个硬件平台和操作系统的内存访问差异以实现Java程序在各个平台下都能达到一致的内存访问效果。



##### 15.4.1 happens-before原则

**1、次序规则**

一个线程内，按照代码顺序，写在前面的操作先行发生于写在后面的操作。

加深说明：前一个操作的结果可以被后续的操作获取。讲直白点，就是前面一个操作把变量x赋值为1，那后面一个操作肯定能知道x已经变成了1。

**2、锁定规则**

一个unLock操作`先行发生`于后面（后面指的是时间上的先后）对同一个锁的lock操作。

如：
```java
public class Demo{
    
    static Object o=new Object();
    
    public static void main(String[] args){
        //对于同一把锁o，threadA一定先unlock同一把锁后threadB才能获得该锁，A现先行发生于B
        synchronized(o){
            //....
        }
    }
}
```


**3、volatile变量规则**

对一个volatile变量的写操作先行发生于后面对这个变量的读操作，<span style="color:red;">前面写的对后面的读是可见的</span>（后面指定是时间上的先后）。

**4、传递规则**

如果操作A先行发生于操作B，操作B先行发生于操作C，那么可以得出操作A先行发生于操作C。

**5、线程启动规则**

Thread对象的start方法先行发生于此线程的每一个动作。

**6、线程中断规则**

对线程interrupt方法的调用先行发生于被中断线程的代码检测到中断事件的发生;<br>
可以通过Thread.interrupt()检查是否发生中断。<br>
也就是说你要先调用interrupt()方法设置中断标志位，我才能检测到中断发送。

**7、线程终止规则**

线程中的所有操作都先行发生于对此线程的终止检测，我们可以通过isAlive()等手段检测线程是否已经终止执行。

**8、对象终结规则**

一个对象的初始化完成（构造函数执行结束）先行发生于它的finalize()方法的开始。<br>
说人话就是，对象还没有完成初始化之前，是不能调用finalize()方法的

##### 15.4.2 volatile和JMM

被volatile修饰的变量有2大特点：`可见性`和`有序性`。

volatile内存定义：<br>
当写一个volatile变量时，JMM会把该线程对应的本地内存中的共享变量值`立即刷新回主内存`中。<br>
当读一个volatile变量时，JMM会把该线程对应的本地内存设置为无效，重新回到主内存中读取最新共享变量的值。<br>
所以volatile的写内存语义就是直接刷新到主内存中，读的内存语义是直接从主内存中读取。

volatile凭什么可以保证可见性和有序性？？  内存屏障（Memoery Barrier）

<br><br>什么是内存屏障？

![img_19.png](studyImgs/img_19.jpg)


#### 15.5 CAS 

原子类：java.util.concurrent.atomic。

没有原子类之前：多线程环境下不使用原子类保证线程安全i++。

使用CAS后：多线程环境下使用原子类保证线程安全i++，类似于乐观锁。

* CAS是什么？

  Java中的CAS全称为Compare and Swap（比较并交换），是一种**非阻塞同步的原子操作**。它用于在多线程环境下实现对共享数据的并发更新，确保更新操作的原子性和一致性。
  CAS操作包含三个操作数：
  **内存位置（V）**：需要读/写的内存值所在的位置。
  **预期原值（A）**：线程期望在该位置上找到的值。
  **新值（B）**：如果内存位置上的值确实与预期原值相匹配，想要更新的新值。

  执行过程如下：
  **1、比较（Compare）**：CAS操作首先会比较内存位置V的当前值与预期原值A是否相等。
  **2、交换（Swap）**：如果相等，则将内存位置V的值更新为新值B；如果不相等，说明其他线程已经修改了该值，当前线程不执行任何操作，但可以重新尝试整个过程（这可能导致自旋锁的出现）。
  CAS常用于实现无锁数据结构和原子类（如AtomicInteger）中，以提高在高并发环境下的性能，减少传统的锁机制带来的上下文切换和阻塞开销。

多个线程同时执行CAS操作只有一个会成功。

CAS缺点：<br>
1、循环时间长，会消耗CPU，消耗时间长，效率低。<br>
2、只能保证一个共享变量的原子操作，不能保证多个变量的原子操作。

#### 15.6 原子类与18罗汉增强

atomic包下一共有16个类，把这些类分一下：

##### 15.6.1 基本类型原子类

> AtomicInteger
> 
> AtomicBoolean
> 
> AtomicLong

常用api介绍：<br>
> public final int get() //获取当前的值
> 
> public final int getAndSet(int newValue) //先获取当前的值,然后设置为newValue
> 
> public final int getAndIncrement() //先获取当前的值,然后自增
> 
> public final int getAndDecrement() //先获取当前的值,然后自减
> 
> public final int getAndAdd(int delta) //先获取当前的值,然后加上预期的值
> 
> public final boolean compareAndSet(int expect, int update) //如果输入数值等于预期值,则原子方式将该值设置为输入值

```java
package com.atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author QRH
 * @date 2024/5/24 21:50
 * @description TODO
 */
public class AtomicIntegerDemo {
    final static  int size=50;
    public static void main(String[] args) throws InterruptedException {
        MyNumber myNumber = new MyNumber();
        CountDownLatch countDownLatch = new CountDownLatch(size);
        for (int i = 1; i <= size; i++) {
            new Thread(() -> {
                try {
                    for (int j = 1; j <= 1000; j++) {
                        myNumber.addPlus();
                    }
                } finally {
                    countDownLatch.countDown();
                }
            }, String.valueOf(i)).start();
        }
        countDownLatch.await();

        System.out.println(Thread.currentThread().getName() + "\t res=" + myNumber.atomicInteger.get());
    }
}

class MyNumber {
    AtomicInteger atomicInteger = new AtomicInteger();

    public void addPlus() {
        atomicInteger.getAndIncrement();
    }
}

```

##### 15.6.2 数组类型原子类

> AtomicIntegerArray
>
> AtomicReferenceArray
>
> AtomicLongArray

```java

package com.atomic;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * @author QRH
 * @date 2024/5/24 22:02
 * @description TODO
 */
public class AtomicIntegerArrayDemo {
    public static void main(String[] args) {
        //a1();

        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(new int[]{1, 2, 3, 4, 5});

        for (int i=0;i<atomicIntegerArray.length();i++){
            System.out.println(atomicIntegerArray.get(i));
        }

        System.out.println("-----------------");

        int tmpId = atomicIntegerArray.getAndSet(0, 1112);
        System.out.println(tmpId+"\t "+atomicIntegerArray.get(0));

        int increment = atomicIntegerArray.getAndIncrement(0);
        System.out.println(increment+"\t "+atomicIntegerArray.get(0));

    }

    private static void a1() {
        //        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(new int[5]);

        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(new int[]{1, 2, 3, 4, 5});

        for (int i=0;i<atomicIntegerArray.length();i++){
            System.out.println(atomicIntegerArray.get(i));
        }

        System.out.println();
    }
}

```

##### 15.6.3 引用类型原子类

> AtomicReference
>
> AtomicStampedReference
>
> AtomicMarkableReference

##### 15.6.4 引用类型原子类

> AtomicIntegerFieldUpdate
>
> AtomicLongFieldUpdate
>
> AtomicReferenceFieldUpdate

使用目的：以一种线程安全的方式操作非线程安全对象内的某些字段。

使用要求：更新的对象属性必须使用public volatile修饰符。因为对象的属性修改类型原子类都是抽象类，所以每次使用都必须使用静态方法newUpdateer()创建一个更新器，并且需要设置想要更新的类和属性。

```java
package com.atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @author QRH
 * @date 2024/5/25 13:44
 * @description TODO
 */
public class AtomicIntegerFieldUpdateDemo {
    public static void main(String[] args) throws Exception {
        int size = 10;
        BankAccount bankAccount = new BankAccount();
        CountDownLatch countDownLatch = new CountDownLatch(size);

        for (int i = 1; i <= size; i++) {
            new Thread(() -> {
                try {
                    for (int j = 1; j <= 1000; j++) {
                        bankAccount.transMoney(bankAccount);
                    }
                } finally {
                    countDownLatch.countDown();
                }
            }, String.valueOf(i)).start();
        }
        countDownLatch.await();
        System.out.println(Thread.currentThread().getName() + "\t res= " + bankAccount.money);
    }
}

class BankAccount {
    String bankName = "CCB";
    public volatile int money = 0;

    AtomicIntegerFieldUpdater<BankAccount> moneyUpdater = AtomicIntegerFieldUpdater.newUpdater(BankAccount.class, "money");

    public void transMoney(BankAccount bankAccount) {
        moneyUpdater.getAndIncrement(bankAccount);
    }
}

```

```java
package com.atomic;

import java.sql.Time;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * @author QRH
 * @date 2024/5/25 13:56
 * @description TODO
 */
public class AtomicReferenceFieldDemo {
    public static void main(String[] args) {
        MyVar myVar = new MyVar();

        for (int i = 1; i <= 5; i++) {
            new Thread(() -> {
                myVar.init(myVar);
            }, String.valueOf(i)).start();
        }

    }
}

class MyVar {
    public volatile Boolean isInit = false;

    AtomicReferenceFieldUpdater<MyVar, Boolean> updater = AtomicReferenceFieldUpdater.newUpdater(MyVar.class, Boolean.class, "isInit");

    public void init(MyVar myVar) {
        if (updater.compareAndSet(myVar, Boolean.FALSE, Boolean.TRUE)) {
            System.out.println(Thread.currentThread().getName() + "\t ---start init need 2 seconds");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "\t ---over init");
        } else {
            System.out.println(Thread.currentThread().getName() + "\t ---已有线程正在初始化工作");
        }
    }
}

```

##### 15.6.5 原子操作增强类

> DoubleAccumulator
>
> DoubleAdder
>
> LongAccumulator 提供自定义的函数操作
> 
> LongAdder 只能用来计算加法，且从0开始计算

```java
package com.atomic;

import lombok.val;

import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author QRH
 * @date 2024/5/25 14:09
 * @description TODO
 */
public class LongAdderDemo {
    public static void main(String[] args) {
        LongAdder longAdder = new LongAdder();

        longAdder.increment();
        longAdder.increment();
        longAdder.increment();

        System.out.println(longAdder.sum());

        System.out.println("--------------");

        LongAccumulator longAccumulator = new LongAccumulator((x, y) -> x + y, 0);

        longAccumulator.accumulate(1);
        longAccumulator.accumulate(12);

        System.out.println(longAccumulator.get());

    }
}

```

```java
package com.atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author QRH
 * @date 2024/5/25 14:18
 * @description TODO
 */
public class AccumulatorCompareDemo {
    public static void main(String[] args) throws Exception{
        ClickNumber clickNumber = new ClickNumber();

        long startTime,endTime;

        CountDownLatch countDownLatch1 = new CountDownLatch(50);
        CountDownLatch countDownLatch2 = new CountDownLatch(50);
        CountDownLatch countDownLatch3 = new CountDownLatch(50);
        CountDownLatch countDownLatch4 = new CountDownLatch(50);


        startTime=System.currentTimeMillis();
        for(int i=1;i<=50;i++){
            new Thread(()->{
              try   {
                  for(int j=1;j<=1_000_000;j++){
                    clickNumber.clickBySynchronized();
                  }
              }finally {
                  countDownLatch1.countDown();
              }
            },String.valueOf(i)).start();
        }
        countDownLatch1.await();
        endTime=System.currentTimeMillis();
        System.out.println("synchronized:"+(endTime-startTime)+"毫秒,number= "+clickNumber.number);

        System.out.println("-------------------");

        startTime=System.currentTimeMillis();
        for(int i=1;i<=50;i++){
            new Thread(()->{
                try   {
                    for(int j=1;j<=1_000_000;j++){
                        clickNumber.clickByAtomicLong();
                    }
                }finally {
                    countDownLatch2.countDown();
                }
            },String.valueOf(i)).start();
        }
        countDownLatch2.await();
        endTime=System.currentTimeMillis();
        System.out.println("AtomicLong:"+(endTime-startTime)+"毫秒,number= "+clickNumber.atomicLong.get());

        System.out.println("-------------------");

        startTime=System.currentTimeMillis();
        for(int i=1;i<=50;i++){
            new Thread(()->{
                try   {
                    for(int j=1;j<=1_000_000;j++){
                        clickNumber.clickByLongAdder();
                    }
                }finally {
                    countDownLatch3.countDown();
                }
            },String.valueOf(i)).start();
        }
        countDownLatch3.await();
        endTime=System.currentTimeMillis();
        System.out.println("LongAdder:"+(endTime-startTime)+"毫秒,number= "+clickNumber.longAdder.sum());

        System.out.println("-------------------");

        startTime=System.currentTimeMillis();
        for(int i=1;i<=50;i++){
            new Thread(()->{
                try   {
                    for(int j=1;j<=1_000_000;j++){
                        clickNumber.clickByLongAccumulator();
                    }
                }finally {
                    countDownLatch4.countDown();
                }
            },String.valueOf(i)).start();
        }
        countDownLatch4.await();
        endTime=System.currentTimeMillis();
        System.out.println("LongAccumulator:"+(endTime-startTime)+"毫秒,number= "+clickNumber.longAccumulator.get());

    }
}

class ClickNumber{
   int number = 0;

   public synchronized void clickBySynchronized(){
       number++;
   }

  AtomicLong atomicLong =  new AtomicLong(0);

  public void clickByAtomicLong(){
      atomicLong.getAndIncrement();
  }

  LongAdder longAdder = new LongAdder();

  public void clickByLongAdder(){
      longAdder.increment();
  }

  LongAccumulator longAccumulator = new LongAccumulator((x,y)->x+y,0);

  public void clickByLongAccumulator(){
      longAccumulator.accumulate(1);
  }


}

```

#### 15.7 Threadlocal

* 是什么？

ThreadLocal提供线程局部变量。这些变量与正常的变量不同，因为每一个线程在访问ThreadLocal实例的时候（通过get或set方法）都有自己的、独立的初始化的变量副本。ThreadLocal实例通常是类中的`私有静态字段`，使用它的目的是希望将状态（例如用户ID或事务ID）与线程关联起来。

* 能干嘛？

实现每一个线程都有自己专属的本地变量副本（自己用自己的变量不麻烦别人，不和其他人共享，人人有份，人各一份），主要解决了让每个线程绑定自己的值，通过使用get或set方法，获取默认值或将其值更改为当前线程所存的副本的值从而避免了线程安全问题，比如我们之前讲解的8锁案例，资源类是使用同一部手机，多个线程抢夺同一部手机使用，假如人手一份是不是天下太平？？？


```java
package com.threadLocal;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @author QRH
 * @date 2024/5/26 15:23
 * @description TODO
 */
public class ThreadLocalDemo {
    public static void main(String[] args) throws  Exception {
        House house = new House();
        CountDownLatch countDownLatch = new CountDownLatch(5);
        for (int i = 1; i <= 5; i++) {
            new Thread(() -> {
                int size = new Random().nextInt(5) + 1;
                try {
                    for (int j = 1; j <= size; j++) {
                        house.saleHouse();
                        house.saleVolumeByThreadLocal();
                    }
                    System.out.println(Thread.currentThread().getName() + "\t号销售卖出了" + house.saleVolume.get() + "间房子");
                } finally {
                  house.saleVolume.remove();
                    countDownLatch.countDown();
                }
            }, String.valueOf(i)).start();
        }
        countDownLatch.await();
        System.out.println(Thread.currentThread().getName() + "\t 共卖出 " + house.saleCount);

    }
}

class House {
    int saleCount = 0;

    public synchronized void saleHouse() {
        ++saleCount;
    }

    ThreadLocal<Integer> saleVolume = ThreadLocal.withInitial(() -> 0);

    public void saleVolumeByThreadLocal() {
        saleVolume.set(1 + saleVolume.get());
    }

}
```

```java
package com.threadLocal;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author QRH
 * @date 2024/5/26 15:23
 * @description TODO
 */
public class ThreadLocalDemo2 {
    public static void main(String[] args) throws Exception {
        MyData myData = new MyData();
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        try {
            for (int i = 0; i < 10; i++) {
                threadPool.submit(() -> {
                    try {
                        Integer beforeCount = myData.threadLocalField.get();
                        myData.add();
                        Integer afterCount = myData.threadLocalField.get();
                        System.out.println(Thread.currentThread().getName() + ": " + beforeCount + "->" + afterCount);
                    } finally {
                        myData.threadLocalField.remove();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }
}

class MyData {

    Integer count = 0;

    ThreadLocal<Integer> threadLocalField = ThreadLocal.withInitial(() -> 0);

    public  void add() {
        threadLocalField.set(1+threadLocalField.get());
    }
}
```

##### 15.7.1 Thread、ThreadLocal、ThreadLocalMap的关系

* ThreadLocalMap是ThreadLocal的私有静态内部类，每个Thread对象都包含一个ThreadLocalMap对象，ThreadLocalMap是ThreadLocal的线程局部变量的Map集合，ThreadLocalMap的key是ThreadLocal对象，value是ThreadLocal对象的value。

##### 15.7.2 ThreadLocal内存泄漏问题

* 什么是内存泄漏？

不再会被使用的对象或变量占用的内存不能被回收，就叫内存泄漏。


##### 15.7.3 总结

1、ThreadLocal并不解决线程之间共享数据的问题。<br>
2、ThreadLocal适用于变量在线程件隔离且在方法间共享的场景。<br>
3、ThreadLocal通过隐式的在不同线程内创建独立实例副本避免了实例线程安全的问题。<br>
4、每个线程持有一个只属于自己的专属Map并维护了ThreadLocal对象与具体实例的映射，该Map由于只被持有它的线程访问，故不存在线程安全以及锁的问题。<br>
4、ThreadLocalMap的Entry对ThreadLocal的引用为弱引用，避免了ThreadLocal对象无法被回收的问题。<br>
5、都会通过expungeStableEntry，cleanSomSlots，replaceStableEntry这三个方法回收键为null的Entry对象的值（及具体的实例）以及Entry对象本身从而防止内存泄漏，属于安全加固的方法。<br>
6、群雄逐鹿起纷争，人各一份天下安。

#### 15.8 synchronized与锁升级

![img_20.png](studyImgs/img_20.png)

![img_21.png](studyImgs/img_21.png)

![img_22.png](studyImgs/img_22.png)


#### 15.9 AQS

* AQS是AbstractQueuedSynchronizer的简称，它是一个抽象类，它提供了一种线程间协作的方式，通过使用AQS的模板方法，实现同步。

<img src="studyImgs/img_23.png" alt="img_23.png"  />



































