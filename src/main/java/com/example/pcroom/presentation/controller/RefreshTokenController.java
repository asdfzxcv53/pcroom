package com.example.pcroom.presentation.controller;

import com.example.pcroom.application.RefreshTokenService;
import com.example.pcroom.domain.RefreshToken;
import com.example.pcroom.presentation.RefreshTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/refreshToken")
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    @Autowired
    public RefreshTokenController(RefreshTokenService refreshTokenService){
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping
    public ResponseEntity<RefreshTokenResponse> createRefreshToken(@RequestParam Long userId){
        RefreshTokenResponse refreshTokenResponse = refreshTokenService.createRefreshToken(userId);

        return ResponseEntity.ok(refreshTokenResponse);
    }

    @GetMapping
    public ResponseEntity<RefreshTokenResponse> findAll(@RequestParam Long userId){
        RefreshTokenResponse refreshTokenResponse = refreshTokenService.findAll(userId);

        return ResponseEntity.ok(refreshTokenResponse);
    }
}
