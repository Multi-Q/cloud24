package com.atguigu.kafka.test;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author QRH
 * @date 2024/5/2 13:19
 * @description TODO
 */
public class KafkaConsumerTest {
    public static void main(String[] args) {
        //配置属性集合
        Map<String,Object> configMap=new HashMap<>();

        //配置kafka集群
        configMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.200.130:9092");

        //配置序列化
        configMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer");
        configMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer");


        //读取数据的位置earliest、latest
        configMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");

        //配置消费者组
        configMap.put(ConsumerConfig.GROUP_ID_CONFIG,"atguigu");

        //自动提交偏移量
        configMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,true);


        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configMap);

        //消费则订阅主题
        consumer.subscribe(Collections.singletonList("kafka_demo_test"));

        while(true){
            ConsumerRecords<String,String> records=consumer.poll(Duration.ofMillis(100));

            //打印捉取数据
            for (ConsumerRecord<String,String> record:records){
                System.out.println("k= "+record.key()+" , val= "+record.value());
            }
        }

    }
}
