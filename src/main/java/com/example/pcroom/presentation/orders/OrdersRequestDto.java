package com.example.pcroom.presentation.orders;

import com.example.pcroom.domain.OrderType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrdersRequestDto {
    private Long userId;
    private OrderType OrdersType; // 0 = 시간 충전, 1 = 음식 주문
    private List<OrdersProductRequestDto> ordersProductRequestDtos = new ArrayList<>();
}
