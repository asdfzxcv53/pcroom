package com.example.pcroom.presentation.user;

import com.example.pcroom.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private String name;
    private String username;
    private String phoneNumber;

    public UserResponseDto fromEntity(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();

        userResponseDto.name = user.getName();
        userResponseDto.username = user.getUsername();
        userResponseDto.phoneNumber = user.getPhoneNumber();

        return userResponseDto;
    }
}
