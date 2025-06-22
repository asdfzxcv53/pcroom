package com.example.pcroom.presentation;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LoginResponseDto {
    private String name;
    private LocalDateTime endTime;

    public LoginResponseDto() {}
    public LoginResponseDto(String name, LocalDateTime endTime) {
        this.name = name;
        this.endTime = endTime;
    }
}
