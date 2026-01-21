package com.example.pcroom.application;

import com.example.pcroom.domain.RefreshToken;
import com.example.pcroom.infrastructure.RefreshTokenRepository;
import com.example.pcroom.infrastructure.UserRepository;
import com.example.pcroom.infrastructure.security.JwtUtil;
import com.example.pcroom.presentation.RefreshTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshTokenResponse createRefreshToken(Long userId) {
        RefreshToken refreshToken = RefreshToken.create(userId, "hashedToken" + userId, Duration.ofDays(14));
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);
        RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse(savedRefreshToken.getUserId(), savedRefreshToken.getHashedToken(), savedRefreshToken.getExpiresAt());

        return refreshTokenResponse;
    }

    public RefreshTokenResponse findAll(Long userId) {

        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElse(null);

        RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse(refreshToken.getUserId(), refreshToken.getHashedToken(), refreshToken.getExpiresAt());

        return refreshTokenResponse;
    }
}
