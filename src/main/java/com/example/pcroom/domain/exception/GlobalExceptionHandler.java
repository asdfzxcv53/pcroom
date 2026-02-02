package com.example.pcroom.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 계정 생성 단계에서 중복이 경우
    @ExceptionHandler(DuplicateAccountException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleDuplicateAccountException(DuplicateAccountException e) {
        return Map.of("error", e.getMessage());
    }

    // 로그인 시도에서 username 이 맞지 않는 경우
    @ExceptionHandler(UsernameNotMatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUsernameNotMatchException(UsernameNotMatchException e) {
        return Map.of("error", e.getMessage());
    }

    // 로그인 시도에서 password 가 맞지 않는 경우
    @ExceptionHandler(PasswordNotMatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handlePasswordNotMatchException(PasswordNotMatchException e) {
        return Map.of("error", e.getMessage());
    }

    // 남은 시간이 없는 경우
    @ExceptionHandler(NoRemainTimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNoRemainTimeException(NoRemainTimeException e) {
        return Map.of("error", e.getMessage());
    }

    // 상품의 수량이 부족한 경우
    @ExceptionHandler(NotEnoughStockException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotEnoughStockException(NotEnoughStockException e) {
        return Map.of("error", e.getMessage());
    }

    // 유저가 사용중이지 않을때
    @ExceptionHandler(NoUserActiveSeatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNoUserActiveSeatException(NoUserActiveSeatException e) {
        return Map.of("error", e.getMessage());
    }

    // Remaintime entity 가 없을때
    @ExceptionHandler(RemainTimeNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleRemainTimeNotFoundException(RemainTimeNotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    // User entity 가 없을때
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUserNotFoundException(UserNotFoundException e) {
        return Map.of("error", e.getMessage());
    }
}
