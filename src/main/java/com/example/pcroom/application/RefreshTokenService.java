package com.example.pcroom.application;

import com.example.pcroom.domain.RefreshToken;
import com.example.pcroom.infrastructure.RefreshTokenRepository;
import com.example.pcroom.presentation.login.RefreshTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // K6 테스트를 위한 메서드
    public RefreshTokenResponse createRefreshToken(Long userId) {
        RefreshToken refreshToken = RefreshToken.create(userId, "hashedToken" + userId, Duration.ofDays(14));
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);
        RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse(savedRefreshToken.getUserId(), savedRefreshToken.getHashedToken(), savedRefreshToken.getExpiresAt());

        return refreshTokenResponse;
    }

    // K6 테스트를 위한 메서드
    public RefreshTokenResponse findByUserId(Long userId) {

        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElse(null);

        RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse(refreshToken.getUserId(), refreshToken.getHashedToken(), refreshToken.getExpiresAt());

        return refreshTokenResponse;
    }
}
