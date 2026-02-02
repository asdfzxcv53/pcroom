package com.example.pcroom.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateAccountException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleDuplicateAccountException(DuplicateAccountException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(UsernameNotMatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUsernameNotMatchException(UsernameNotMatchException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handlePasswordNotMatchException(PasswordNotMatchException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(NoRemainTimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNoRemainTimeException(NoRemainTimeException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(NotEnoughStockException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotEnoughStockException(NotEnoughStockException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(NoUserActiveSeatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNoUserActiveSeatException(NoUserActiveSeatException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(RemainTimeNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleRemainTimeNotFoundException(RemainTimeNotFoundException e) {
        return Map.of("error", e.getMessage());
    }
}
