package com.example.pcroom.presentation.login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    private String username;
    private String password;
    private int seatNumber;

    public LoginRequestDto() {}
    public LoginRequestDto(String username, String password, int seatNumber) {
        this.username = username;
        this.password = password;
        this.seatNumber = seatNumber;
    }
}
