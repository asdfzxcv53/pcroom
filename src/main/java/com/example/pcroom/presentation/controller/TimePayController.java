package com.example.pcroom.presentation.controller;

import com.example.pcroom.application.KakaoPayService;
import com.example.pcroom.application.OrdersService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/time/pay")
public class TimePayController {

    private final KakaoPayService kakaoPayService;
    private final OrdersService ordersService;

    public TimePayController(KakaoPayService kakaoPayService, OrdersService ordersService) {
        this.kakaoPayService = kakaoPayService;
        this.ordersService = ordersService;
    }

    @PostMapping
    public void addTime() {

    }
}
