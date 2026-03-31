package com.example.pcroom.presentation.controller;

import com.example.pcroom.application.AuthService;
import com.example.pcroom.application.LogoutService;
import com.example.pcroom.presentation.login.LoginRequestDto;
import com.example.pcroom.presentation.login.LoginResponseDto;
import com.example.pcroom.presentation.login.LoginResult;
import com.example.pcroom.presentation.login.ReissueResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
public class AuthController {

    private final AuthService authService;
    private final LogoutService logoutService;

    @Autowired
    public AuthController (AuthService authService, LogoutService logoutService) {
        this.authService = authService;
        this.logoutService = logoutService;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        LoginResult loginResult = authService.login(loginRequestDto);

        String accessToken = loginResult.getAccessToken();
        String refreshToken = loginResult.getRefreshToken();

        ResponseCookie refreshCookie = ResponseCookie.from(
                "refreshtoken", refreshToken
                )
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(14))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        LoginResponseDto loginResponseDto = new LoginResponseDto(loginResult.getUserSummary(), accessToken);

        return ResponseEntity.ok(loginResponseDto);
    }

    @PostMapping(value = "/reissue")
    public ResponseEntity<?> reissue(@CookieValue("refreshToken") String refreshToken) {
        ReissueResponse reissueResponse = authService.reissue(refreshToken);

        return ResponseEntity.ok(reissueResponse);
    }

    @PatchMapping(value = "/logout/{userId}")
    public ResponseEntity<?> logout(@PathVariable Long userId, @CookieValue("refreshToken") String refreshToken) {
        logoutService.logoutUser(userId, refreshToken);

        return ResponseEntity.ok().build();
    }
}
