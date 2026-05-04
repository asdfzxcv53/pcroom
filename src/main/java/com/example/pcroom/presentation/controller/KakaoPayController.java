package com.example.pcroom.presentation.controller;

import com.example.pcroom.application.KakaoPayService;
import com.example.pcroom.presentation.kakao.KakaoApproveResponse;
import com.example.pcroom.presentation.kakao.KakaoReadyResponse;
import com.example.pcroom.presentation.orders.OrdersCancelResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pay")
public class KakaoPayController {

    private final KakaoPayService kakaoPayService;

    @Autowired
    KakaoPayController(KakaoPayService kakaoPayService) {
        this.kakaoPayService = kakaoPayService;
    }

    @PostMapping("/ready")
    public ResponseEntity<KakaoReadyResponse> readyPay(@RequestParam Long orderId) {
        KakaoReadyResponse response = kakaoPayService.ready(orderId);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/success")
    public ResponseEntity<KakaoApproveResponse> approvePay(@RequestParam String pg_token, @RequestParam Long orderId){
        KakaoApproveResponse response = kakaoPayService.approve(pg_token, orderId);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/cancel") // 카카오페이 결제 전 redirect_cancel
    public String cancelBeforePay(@RequestParam Long orderId){
        kakaoPayService.cancelBeforePay(orderId);

        return "pay canceled";
    }
    @PostMapping("/refund") // 카카오페이 결제 후 사용자 요청에 의한 cancel
    public ResponseEntity<OrdersCancelResponse> cancelAfterPay(@RequestParam Long orderId){
        OrdersCancelResponse response = kakaoPayService.cancelAfterPay(orderId);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/fail")
    public String failPay(@RequestParam Long orderId) {
        kakaoPayService.fail(orderId);

        return "fail success";
    }
}
