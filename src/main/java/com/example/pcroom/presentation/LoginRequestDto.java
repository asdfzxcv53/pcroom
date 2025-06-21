package com.example.pcroom.presentation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    private String username;
    private String password;
    private int seatNumber;
}
