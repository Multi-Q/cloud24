package com.redis6.springdata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.MainRedis;
import jakarta.annotation.Resource;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;

/**
 * @author QRH
 * @date 2024/6/19 10:43
 * @description TODO
 */
@SpringBootTest(classes = MainRedis.class)
public class StringRedisTemplateTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private ObjectMapper mapper=new ObjectMapper();

    @Test
    public void testUser() throws JsonProcessingException {
        User user = new User();
        user.setName("王五");
        user.setAge(45);
        user.setSex("男");
        //手动序列化
        String s = mapper.writeValueAsString(user);
        stringRedisTemplate.opsForValue().set("user:4",s);

        String jsonUser = stringRedisTemplate.opsForValue().get("user:4");
        //手动反序列化
        User user1 = mapper.readValue(jsonUser, User.class);
        System.out.println(user1);

    }

    @Test
    public void testHash(){
        stringRedisTemplate.opsForHash().put("user:5","name","小怪兽");
        System.out.println(stringRedisTemplate.opsForHash().get("user:5", "name"));

        HashMap<String,String> map=new HashMap<String,String>();
        map.put("name","文星");
        map.put("age","23");
        map.put("sex","男");
        map.put("phoneNumber","123456789");
        stringRedisTemplate.opsForHash().putAll("user:6",map);
        System.out.println(stringRedisTemplate.opsForHash().entries("user:6"));
    }



    @Data
   static class User{
        private String name;
        private String sex;
        private Integer age;
    }
}
