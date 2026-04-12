package com.example.pcroom.domain.exception;

public class OutboxPayloadSerializationException extends RuntimeException{
    public OutboxPayloadSerializationException( String message ) { super(message); }
}
