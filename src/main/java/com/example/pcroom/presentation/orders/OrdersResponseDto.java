package com.example.pcroom.presentation.orders;

import com.example.pcroom.domain.Orders;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrdersResponseDto {

    private Long orderId;
    private LocalDateTime orderTime;
    private int quantity;
    private List<OrdersProductResponseDto> ordersProductResponseDtos = new ArrayList<>();
}
