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
>> **ResultData.class**

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
    public ResultData<String> exception(Exception e) {
        System.out.println("来到全局异常处理器了");
        log.error("全局异常信息：{}", e.getMessage(), e);
        return ResultData.fail(ReturnCodeEnum.RC500.getCode(), e.getMessage());
    }
}

```

## 第三天

### 一、Consul服务注册与发现

* 为什么要引入服务注册中心？<br>
  实现微服务之间的动态注册与发现

Consul需要从官网下载（https://developer.hashicorp.com/consul/install） ，安装到本地,验证是否安装成功： 到安装包所在的目录，打开cmd，输入`consul --version`
，如果出现一下信息表示成功。

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

当Consul服务关闭时，再次进入页面之前的配好的配置就会全没有，所以需要将Consul持久化。（持久化配置将在之后进行）

## 第三天

### 一、LoadBalancer负载均衡

spring cloud LoadBalancer没有专门的jar包，它挂载在`Spring-Cloud-Commons`jar包下。

* LB负载均衡是什么？

简单来讲就是将用户的请求平摊的分配到多个服务器上，从而达到系统的HA（高可用），常见的负载均衡有软件Nginx、LVS和硬件F5。

* spring-cloud-starter-loadbalancer是什么？

这是Spring Cloud官方提供的一个开源的、简易的客户端负载均衡器，它包含在Spring Cloud Commons中用来替代以前的Ribbon组件。相较于Ribbon，Spring Cloud
LoadBalancer不经能支持`RestTemplate`，还支持`WebClient`（WebClient是Spring Web Flux中提供的功能，可以实现响应式异步请求）。

### 二、完成Consul的数据持久化

#### 1、Consul数据持久化配置并注册为Window服务

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

### 三、开始使用LoadBalancer

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

### 四、OpenFeign服务接口调用

* OpenFeign是什么？

  Feign是一个<span style="color:red;font-weight:bolder;font-size:20px;">`声明式web服务客户端`</span>
  。他编写web服务客户端变得更容易。`使用Feign创建一个接口并对其进行注释`。它具有可插入的注释支持，包括Feign注释和JAX-RS注释。Feign还支持可插拔编码器和解码器。Spring Cloud添加了对Spring
  MVC注释的支持，以及对使用Spring Web中默认使用的HttpMessageConveter的支持。Spring Cloud还集成了Eureka、Spring Cloud CircuitBreaker以及Spring Cloud
  LoadBalancer，以便使用Feign时提供负载均衡的http客户端。


* 已经有了loadbalancer为什么还要学OpenFeign？日常用哪个？

  日常用OpenFeign

#### 1、OpenFeign通用步骤

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

### 五、OpenFeign高级特性

#### 1、OpenFeign超时配置

OpenFign默认等待时间：60s，超时报错

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

那为单个服务设置超时时间该如何做呢？ 步骤： ①在`cloud-consumer-feign-order80`项目中的controller头上天剑指定的`微服务服务实例`

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

#### 2、OpenFign重试机制

重试机制默认是`关闭的`，开启重试机制需写个配置类

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
        //最大请求次数为3，出时间间隔时间为100ms，重试最大间隔时间为1s
        return new Retryer.Default(100, 1, 3);
    }
}

```

OpenFign的重试次数在控制台看不到，只是给出了最终结果。如果想要看到每次重试的结果，将在日志打印那学到

#### 3、OpenFign默认HttpClient修改

OpenFign中的Http Client如果不做特殊配置，则会默认使用JDK自带的HttpURLConnection发送HTTP请求。<br>
但，由于默认的HttpURLConnection没有连接池，性能和效率比较低，如果采用默认，性能不是最牛的，所以要加到最大。

所以使用Apache HttpClient5替换HttpURLConnection

步骤：

①修改`消费者模块(cloud-consumer-feign-order80)的pom.xml`，引入`httpclient5`依赖

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

②修改`消费者模块(cloud-consumer-feign-order80)的application.yml`，配置Apache HttpClient5

```yml
spring:
  cloud:
    openfign:
      httpclient:
        hc5:
          enabled: true

```

#### 4、OpenFign请求/压缩功能

对请求和响应进行GZIP压缩，以减少同行过程中的性能损耗

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

#### 5、OpenFign日志打印功能

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

### 六、CirCuitBreaker断路器

断路器：当某个服务不可用时，会自动切换到备用服务。

CirCuitBreaker只是一套规范或接口，落实实现是`Resiliences4j`

Resiliences4j是什么？

* Resiliences4j是容错库

#### 6.1 熔断（CirCuitBreaker）
##### 6.1.1 按照COUNT_BASE

步骤：

①在提供者模块`cloud-provider-payment8001`新增PayCircuitController.java

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

③消费者模块`cloud-consumer-feign-order80`添加Resilience4j的依赖

```xml

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>
        <!--        由于短路保护需要aop实现，所以必须导入aop包-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>

```

④编写yml
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
        automaticTransitionFromOpenToHalfOpenEnabled: true #是否启用自动从开启状态过渡到半开状态，默认值为true，如果启用，circuitbreaker
        permittedNumberOfCallsInHalfOpenState: 2 #半开状态允许的最大请求数，默认为10
        recordExceptions:
          - java.lang.Exception
    instances:
      cloud-payment-service:
        baseConfig: default #使用默认配置
```

⑤新建OrderCircuitController.java
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
    @CircuitBreaker(name="cloud-payment-service",fallbackMethod = "myCircuitFallback")
    public String myCircuitBreaker(@PathVariable("id") Integer id){
        return  payFeignApi.myCircuit(id);
    }
    
    //myCircuitFallback就是服务熔断降级后的兜底处理方法
    public String myCircuitFallback(Integer id,Throwable t){
        return "myCircuitFallback，系统繁忙，请稍后重试----~~~~";
    }
}

```

⑥测试

##### 6.1.2 按照TIME_BASED
步骤：
①修改yml
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

##### 6.1.3 COUNT_BASED和TIME_BASED用哪个？
建议使用COUNT_BASED

#### 6.2 隔离（BuldHead）
隔离是什么？
* 限制并发

隔离能干什么？
* 用来限制对于下游服务的并发请求数

Resilience4j提供了两种隔离的实现：

##### 6.2.1 Semahore信号量




