package com.example.pcroom.domain.exception;

public class UsernameNotMatchException extends RuntimeException {
    public UsernameNotMatchException(String message) {
        super(message);
    }
}
