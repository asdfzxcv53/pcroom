package com.example.pcroom.presentation.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {

    private String username;
    private String password;
    private String name;
    private String phoneNumber;

    public UserRequestDto() {}

    public UserRequestDto(String username, String password, String name, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
