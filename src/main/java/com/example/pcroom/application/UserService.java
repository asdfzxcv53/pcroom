package com.example.pcroom.application;

import com.example.pcroom.domain.User;
import com.example.pcroom.domain.exception.DuplicateAccountException;
import com.example.pcroom.infrastructure.UserRepository;
import com.example.pcroom.presentation.user.UserRequestDto;
import com.example.pcroom.presentation.user.UserResponseDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDto addUser(UserRequestDto userRequestDto) {

        checkDuplicatioAccount(userRequestDto.getUsername());

        User user = User.builder()
                .username(userRequestDto.getUsername())
                .password(userRequestDto.getPassword())
                .name(userRequestDto.getName())
                .phoneNumber(userRequestDto.getPhoneNumber())
                .build();

        User savedUser = userRepository.save(user);

        UserResponseDto userResponseDto = new UserResponseDto();

        return userResponseDto.fromEntity(savedUser);
    }

    public void checkDuplicatioAccount(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    throw new DuplicateAccountException("이미 존재하는 아이디 입니다.");
                });
    }
}
