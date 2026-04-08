package com.example.pcroom.domain.exception;

public class SeatNotFoundException extends RuntimeException {
    public SeatNotFoundException( String message ) { super(message); }
}
