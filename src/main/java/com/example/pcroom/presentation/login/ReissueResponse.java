package com.example.pcroom.presentation.login;

import com.example.pcroom.presentation.user.UserSummary;
import lombok.Getter;

@Getter
public class ReissueResponse {

    private String newAccessToken;
    private UserSummary userSummary;

    public ReissueResponse() {}
    public ReissueResponse(String newAccessToken, UserSummary userSummary) {
        this.newAccessToken = newAccessToken;
        this.userSummary = userSummary;
    }
}
