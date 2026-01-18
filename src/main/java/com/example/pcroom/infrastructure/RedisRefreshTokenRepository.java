package com.example.pcroom.infrastructure;

import com.example.pcroom.domain.RefreshToken;
import com.example.pcroom.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

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

    private String generateKey(Long userId, String hashedToken) {
        return KEY_PREFIX + userId + ":" + hashedToken;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        String key = generateKey(refreshToken.getUserId(), refreshToken.getHashedToken());

        long ttlSeconds = Duration.between(LocalDateTime.now(), refreshToken.getExpiresAt()).toSeconds();

        redisTemplate.opsForValue().set(key, refreshToken, ttlSeconds, TimeUnit.SECONDS);

        return refreshToken;
    }

    @Override
    public void deleteByUserId(Long userId) {
        String pattern = KEY_PREFIX + userId + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        if(keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Override
    public Optional<RefreshToken> findByUserIdAndHashedToken(Long userId, String hashedToken) {
        String key = generateKey(userId, hashedToken);

        RefreshToken refreshToken = (RefreshToken) redisTemplate.opsForValue().get(key);
        if(refreshToken != null) {
            return Optional.of(refreshToken);
        }
        return Optional.empty();
    }
}
