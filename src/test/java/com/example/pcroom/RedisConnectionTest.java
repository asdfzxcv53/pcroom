package com.example.pcroom;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class RedisConnectionTest {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Test
    @DisplayName("레디스 연결 테스트")
    void redis_connection_test() {
        redisTemplate.opsForValue().set("test:key", "hello redis");
        String value = (String)redisTemplate.opsForValue().get("test:key");

        assertThat(value).isEqualTo("hello redis");
    }
}
