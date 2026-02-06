package com.example.pcroom.presentation.orders;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrdersCancelResponse {
    private Long orderId;
    private Long userId;
    private Integer CancelAmount;
    private LocalDateTime orderDate;

    public OrdersCancelResponse() {}
    public OrdersCancelResponse(Long orderId, Long userId, Integer CancelAmount, LocalDateTime orderDate) {
        this.orderId = orderId;
        this.userId = userId;
        this.CancelAmount = CancelAmount;
        this.orderDate = orderDate;
    }
}
