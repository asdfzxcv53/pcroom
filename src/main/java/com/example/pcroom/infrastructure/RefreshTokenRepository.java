package com.example.pcroom.infrastructure;

import com.example.pcroom.domain.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    public RefreshToken save(RefreshToken refreshToken);
    public void deleteByUserId(Long userId);
    public Optional<RefreshToken> findByUserId(Long userId);
}
