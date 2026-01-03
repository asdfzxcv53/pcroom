package com.example.pcroom.presentation.controller;

import com.example.pcroom.application.LoginService;
import com.example.pcroom.application.LogoutService;
import com.example.pcroom.application.SchedulerService;
import com.example.pcroom.application.UserService;
import com.example.pcroom.infrastructure.security.JwtUtil;
import com.example.pcroom.presentation.LoginRequestDto;
import com.example.pcroom.presentation.LoginResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final LoginService loginService;
    private final LogoutService logoutService;

    @Autowired
    public AuthController (AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserDetailsService userDetailsService, LoginService loginService, LogoutService logoutService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.loginService = loginService;
        this.logoutService = logoutService;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));

            // 인증 성공시

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateJwtToken(userDetails);

            LoginResponseDto loginResponseDto = loginService.login(loginRequestDto);

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(loginResponseDto);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PatchMapping(value = "/logout/{userId}")
    public ResponseEntity<?> logout(@PathVariable Long userId) {
        logoutService.logoutUser(userId);

        return ResponseEntity.ok().build();
    }
}
