package com.example.pcroom.domain.exception;

public class KakaoPayCantFailException extends RuntimeException {
    public KakaoPayCantFailException( String message ) { super(message); }
}
