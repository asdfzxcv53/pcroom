package com.example.pcroom.presentation;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RefreshTokenResponse {
    private Long userId;
    private String hashedToken;
    private LocalDateTime expiresAt;

    public RefreshTokenResponse() {}
    public RefreshTokenResponse(Long userId, String hashedToken, LocalDateTime expiresAt) {
        this.userId = userId;
        this.hashedToken = hashedToken;
        this.expiresAt = expiresAt;
    }
}
