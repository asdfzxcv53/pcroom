package com.example.pcroom.presentation.login;

import com.example.pcroom.presentation.user.UserSummary;
import lombok.Getter;
import lombok.Setter;

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
