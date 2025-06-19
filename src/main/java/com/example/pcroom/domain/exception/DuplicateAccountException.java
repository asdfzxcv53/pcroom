package com.example.pcroom.domain.exception;

public class DuplicateAccountException extends RuntimeException {
    public DuplicateAccountException(String message) { super(message); }
}
