package com.example.pcroom.application;

import com.example.pcroom.domain.User;
import com.example.pcroom.domain.exception.PasswordNotMatchException;
import com.example.pcroom.domain.exception.UsernameNotMatchException;
import com.example.pcroom.infrastructure.SeatHistoryRepository;
import com.example.pcroom.infrastructure.UserRepository;
import com.example.pcroom.presentation.LoginRequestDto;
import com.example.pcroom.presentation.LoginResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private final UserRepository userRepository;
    private final SeatHistoryRepository seatHistoryRepository;

    @Autowired
    public LoginService(UserRepository userRepository, SeatHistoryRepository seatHistoryRepository) {
        this.userRepository = userRepository;
        this.seatHistoryRepository = seatHistoryRepository;
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

        checkValidation(loginRequestDto);


    }

    private void checkValidation(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        Optional<User> user = userRepository.findByUsername(username);

        if(user.isPresent()) {
            if(!user.get().getPassword().equals(password)) {
                throw(new PasswordNotMatchException());
            }
        } else {
            throw(new UsernameNotMatchException());
        }
    }
}
