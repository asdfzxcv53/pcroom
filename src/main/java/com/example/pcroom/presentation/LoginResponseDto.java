package com.example.pcroom.presentation;

import com.example.pcroom.presentation.user.UserSummary;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LoginResponseDto {
    private UserSummary userSummary;
    private String accessToken;

    public LoginResponseDto() {}
    public LoginResponseDto(UserSummary userSummary, String accessToken) {
        this.userSummary = userSummary;
        this.accessToken = accessToken;
    }
}
