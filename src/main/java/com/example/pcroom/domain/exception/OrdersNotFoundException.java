package com.example.pcroom.domain.exception;

public class OrdersNotFoundException extends RuntimeException {
    public OrdersNotFoundException(String message) { super(message); }
}
