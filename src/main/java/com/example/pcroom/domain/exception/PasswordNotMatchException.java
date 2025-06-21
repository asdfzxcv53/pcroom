package com.example.pcroom.domain.exception;

public class PasswordNotMatchException extends RuntimeException {
    public PasswordNotMatchException() {
        super("Password not match");
    }
}
