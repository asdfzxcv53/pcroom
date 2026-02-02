package com.example.pcroom.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

//@Entity
@Getter
public class RefreshToken {

//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "REFRESHTOKEN_ID")
//    private Long id;

//    @Column
    private Long userId;

//    @Column(nullable = false, length = 255)
    private String hashedToken;

//    @Column(nullable = false)
    private LocalDateTime expiresAt;

//    @Column(nullable = false)
    private boolean revoked;

//    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public RefreshToken() {}
    protected RefreshToken(Long userId, String hashedToken, Duration ttl) {
        this.userId = userId;
        this.hashedToken = hashedToken;
        this.expiresAt = LocalDateTime.now().plus(ttl);
        this.revoked = false;
        this.createdAt = LocalDateTime.now();
    }

    public static RefreshToken create(Long userId, String hashedToken, Duration ttl) {
        return new RefreshToken(userId, hashedToken, ttl);
    }

    @JsonIgnore
    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    public void revoke() {
        this.revoked = true;
    }

    @JsonIgnore
    public boolean isRevoked() {
        return revoked;
    }

}
