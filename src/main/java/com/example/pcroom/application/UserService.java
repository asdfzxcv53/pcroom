package com.example.pcroom.application;

import com.example.pcroom.domain.RemainTime;
import com.example.pcroom.domain.Role;
import com.example.pcroom.domain.User;
import com.example.pcroom.domain.exception.DuplicateAccountException;
import com.example.pcroom.infrastructure.RemainTimeRepository;
import com.example.pcroom.infrastructure.UserRepository;
import com.example.pcroom.presentation.user.UserRequestDto;
import com.example.pcroom.presentation.user.UserResponseDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RemainTimeRepository remainTimeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RemainTimeRepository remainTimeRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.remainTimeRepository = remainTimeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto addUser(UserRequestDto userRequestDto) {     //  회원가입

        checkDuplicatioAccount(userRequestDto.getUsername());

        User user = User.builder()
                .username(userRequestDto.getUsername())
                .password(passwordEncoder.encode(userRequestDto.getPassword()))
                .name(userRequestDto.getName())
                .phoneNumber(userRequestDto.getPhoneNumber())
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        // 회원가입 순간에 remainTime 테이블에 그 회원의 남은시간 레코드 삽입
        RemainTime remainTime = new RemainTime(savedUser);
        remainTimeRepository.save(remainTime);

        UserResponseDto userResponseDto = new UserResponseDto();

        return userResponseDto.fromEntity(savedUser);
    }

    public Long addRemainTime(Long userId, Long addTime) {
        Optional<RemainTime> remainTime = remainTimeRepository.findRemainTime(userId);

        remainTime.get().addRemainTime(addTime);

        return remainTime.get().getRemainTime();
    }

    public void checkDuplicatioAccount(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    throw new DuplicateAccountException("이미 존재하는 아이디 입니다.");
                });
    }
}
