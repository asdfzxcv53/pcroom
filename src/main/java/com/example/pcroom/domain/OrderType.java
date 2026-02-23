package com.example.pcroom.domain;

public enum OrderType {
    TIME(0),
    FOOD(1);

    private int value;

    OrderType(int value) {
        this.value = value;
    }
}
