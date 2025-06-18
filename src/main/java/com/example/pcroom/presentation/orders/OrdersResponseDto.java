package com.example.pcroom.presentation.orders;

import com.example.pcroom.domain.Orders;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrdersResponseDto {

    private Long orderId;
    List<OrdersProductResponseDto> ordersProductResponseDtos = new ArrayList<>();

    public void addOrdersProductResponseDto(OrdersProductResponseDto ordersProductResponseDto) {
        ordersProductResponseDtos.add(ordersProductResponseDto);
    }
}
