package com.atguigu.kafka.test;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.HashMap;
import java.util.Map;

/**
 * @author QRH
 * @date 2024/5/2 12:28
 * @description TODO
 */
public class KafkaProducerTest {
    public static void main(String[] args) {
        //配置属性集合
        Map<String,Object> configMap=new HashMap<>();

        //kafka集群地址
        configMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.200.130:9092");

        //数据序列化
        configMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        configMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");

        //创建kafka对象
        KafkaProducer<String,String> producer=new KafkaProducer(configMap);

        //创建Topic
        ProducerRecord<String, String> record = new ProducerRecord<>("kafka_demo_test", "key1", "value1");

        //生产数据
        producer.send(record);



        //关闭连接
        producer.close();

    }
}
