package com.atguigu.test;

import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClient;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.MessageSource;

import java.util.List;

@SpringBootTest
public class NacosTest {

    @Resource
    private DiscoveryClient discoveryClient;

    @Resource
    private NacosDiscoveryClient nacosDiscoveryClient;
    @Qualifier("messageSource")
    @Autowired
    private MessageSource messageSource;

    @Test
    public void discoveryClientTest(){
        for (String service:discoveryClient.getServices()){
            System.out.println("service = " + service);

            //获取port
            List<ServiceInstance> instances = discoveryClient.getInstances(service);
            for (ServiceInstance instance:instances){
                System.out.println("ip = " + instance.getHost()+ "port = " + instance.getPort());
            }

        }
    }


    @Test
    public void nacosDiscoveryClientTest(){
        for (String service:nacosDiscoveryClient.getServices()){
            System.out.println("service = "+service);

            //获取port
            List<ServiceInstance> instances = nacosDiscoveryClient.getInstances(service);
            for (ServiceInstance instant:instances){
                System.out.println("ip = "+instant.getHost()+ " port = "+instant.getPort());
            }
        }
    }
}
