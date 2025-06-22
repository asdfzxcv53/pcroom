package com.example.pcroom.domain.exception;

public class NoRemainTimeException extends RuntimeException {
    public NoRemainTimeException(String message){
        super(message);
    }
}
