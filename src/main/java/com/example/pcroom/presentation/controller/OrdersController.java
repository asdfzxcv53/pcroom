package com.example.pcroom.presentation.controller;

import com.example.pcroom.application.OrdersService;
import com.example.pcroom.domain.Orders;
import com.example.pcroom.presentation.orders.OrdersRequestDto;
import com.example.pcroom.presentation.orders.OrdersResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.List;

@RestController
@RequestMapping(value = "/orders")
public class OrdersController {

    private final OrdersService ordersService;

    @Autowired
    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @PostMapping
    public ResponseEntity<OrdersResponseDto> createOrder(@RequestBody OrdersRequestDto ordersRequestDto){

        OrdersResponseDto ordersResponseDto = ordersService.createOrder(ordersRequestDto);

        return ResponseEntity.ok(ordersResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<OrdersResponseDto>> getOrdersByUserId(@RequestParam("userId") Long userId){

        List<OrdersResponseDto> ordersResponseDtos = ordersService.getOrdersByUserId(userId);

        return ResponseEntity.ok(ordersResponseDtos);
    }
}
