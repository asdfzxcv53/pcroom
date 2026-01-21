package com.example.pcroom.infrastructure;

import com.example.pcroom.domain.RefreshToken;
import com.example.pcroom.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Ref;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@Profile("redis")
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

    private static final String KEY_PREFIX = "refresh_token:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisRefreshTokenRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String generateKey(Long userId) {
        return KEY_PREFIX + userId;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        String key = generateKey(refreshToken.getUserId());

        long ttlSeconds = Duration.between(LocalDateTime.now(), refreshToken.getExpiresAt()).toSeconds();

        redisTemplate.opsForValue().set(key, refreshToken, ttlSeconds, TimeUnit.SECONDS);

        return refreshToken;
    }

    @Override
    public void deleteByUserId(Long userId) {
        String key = generateKey(userId);
        redisTemplate.delete(key);
    }

    @Override
    public Optional<RefreshToken> findByUserId(Long userId) {
        String key = generateKey(userId);
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            return Optional.of((RefreshToken) value);
        }
        return Optional.empty();
    }
}
