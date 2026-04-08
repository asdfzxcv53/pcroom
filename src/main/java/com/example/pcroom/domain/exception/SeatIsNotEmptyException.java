package com.example.pcroom.domain.exception;

public class SeatIsNotEmptyException extends RuntimeException {
    public SeatIsNotEmptyException(String message ) { super(message); }
}
