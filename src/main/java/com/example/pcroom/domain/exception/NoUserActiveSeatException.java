package com.example.pcroom.domain.exception;

public class NoUserActiveSeatException extends RuntimeException {
    public NoUserActiveSeatException(String message) {
        super(message);
    }
}
