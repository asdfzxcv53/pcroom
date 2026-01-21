package com.example.pcroom.infrastructure.initializer;

import com.example.pcroom.domain.RefreshToken;
import com.example.pcroom.domain.Role;
import com.example.pcroom.domain.User;
import com.example.pcroom.infrastructure.RefreshTokenRepository;
import com.example.pcroom.infrastructure.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Profile("redis")
public class RedisRefreshTokenDataLoader implements CommandLineRunner {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Autowired
    public RedisRefreshTokenDataLoader(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        for(int i = 1; i <= 1000; i++){
            User user = new User("asdf"+i, "1234", "User"+i, "010"+i, Role.USER);
            User savedUser = userRepository.save(user);

            RefreshToken refreshToken = RefreshToken.create(
                    user.getId(),
                    "hashed_token"+savedUser.getId(),
                    Duration.ofDays(14)
            );

            refreshTokenRepository.save(refreshToken);
        }
    }
}
