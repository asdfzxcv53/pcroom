package com.example.pcroom.presentation.controller;

import com.example.pcroom.application.LoginService;
import com.example.pcroom.presentation.LoginRequestDto;
import com.example.pcroom.presentation.LoginResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = loginService.login(loginRequestDto);

        return ResponseEntity.ok(loginResponseDto);
    }
}
