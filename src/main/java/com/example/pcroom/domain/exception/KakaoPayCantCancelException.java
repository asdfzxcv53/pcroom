package com.example.pcroom.domain.exception;

public class KakaoPayCantCancelException extends RuntimeException {
    public KakaoPayCantCancelException(String message) { super(message); }
}
