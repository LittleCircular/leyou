package com.leyou;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LyUserService.class)
public class RedisTest {

    @Autowired
    private StringRedisTemplate template;

    @Test
    public void test1(){

        //string
        ValueOperations<String, String> stringStringValueOperations = template.opsForValue();
        stringStringValueOperations.set("name","tom");
    }

    @Test
    public void test2(){

        //string
        ValueOperations<String, String> stringStringValueOperations = template.opsForValue();
        stringStringValueOperations.set("age","11",1, TimeUnit.MINUTES);


    }

    @Test
    public void test3(){

        //hash
        BoundHashOperations<String, Object, Object> boundHashOperations = template.boundHashOps("myH=hash1");
        boundHashOperations.put("address","beijing");
        boundHashOperations.put("school","wuda");

        Object address = boundHashOperations.get("address");
        System.out.println(address);

        Map<Object, Object> entries = boundHashOperations.entries();
        for (Map.Entry<Object, Object> o : entries.entrySet()) {
            System.out.println(o.getKey()+"==="+o.getValue());
        }
    }
}
