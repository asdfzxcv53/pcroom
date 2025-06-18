package com.example.pcroom.presentation.orders;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrdersRequestDto {
    private Long userId;
    List<OrdersProductRequestDto> ordersProductRequestDtos = new ArrayList<>();
}
