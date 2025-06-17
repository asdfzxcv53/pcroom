package com.example.pcroom.presentation.orders;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrdersResponseDto {

    List<OrdersProductResponseDto> ordersProductResponseDtos;

    public void addOrdersProductResponseDto(OrdersProductResponseDto ordersProductResponseDto) {
        ordersProductResponseDtos.add(ordersProductResponseDto);
    }
}
