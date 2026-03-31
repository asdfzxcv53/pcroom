package com.example.pcroom.presentation.login;

import com.example.pcroom.presentation.user.UserSummary;
import lombok.Getter;

@Getter
public class LoginResult {
    private String accessToken;
    private String refreshToken;
    private UserSummary userSummary;

    public LoginResult(String accessToken, String refreshToken, UserSummary userSummary) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userSummary = userSummary;
    }
}
