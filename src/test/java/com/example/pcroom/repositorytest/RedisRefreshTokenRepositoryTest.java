package com.example.pcroom.repositorytest;

import com.example.pcroom.domain.RefreshToken;
import com.example.pcroom.domain.Role;
import com.example.pcroom.domain.User;
import com.example.pcroom.infrastructure.RedisRefreshTokenRepository;
import com.example.pcroom.testconfig.RedisTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Ref;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataRedisTest
@Import({RedisRefreshTokenRepository.class, RedisTestConfig.class})
@ActiveProfiles("redis")
public class RedisRefreshTokenRepositoryTest {

    @Autowired
    private RedisRefreshTokenRepository redisRefreshTokenRepository;

    @Test
    @DisplayName("토큰 저장")
    public void tokenSaveTest() {

        //Given

        String prefix = "refresh_token:";
        User user = new User("abc123", "1234", "승우", "01012341234", Role.USER);

        RefreshToken refreshToken = RefreshToken.create(user.getId(), "tokenhash", Duration.ofDays(14));
        String key = prefix + user.getId();

        //When

        redisRefreshTokenRepository.save(refreshToken);

        // then

        RefreshToken found = redisRefreshTokenRepository.findByUserId(user.getId())
                .orElse(null);

        assertThat(found.getHashedToken()).isEqualTo(refreshToken.getHashedToken());
    }
}
