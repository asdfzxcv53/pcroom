package com.example.pcroom.domain.exception;

public class UsernameNotMatchException extends RuntimeException {
    public UsernameNotMatchException() {
        super("Username not match");
    }
}
