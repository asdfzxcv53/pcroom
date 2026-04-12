package com.example.pcroom.application;

import com.example.pcroom.domain.KakaoPayProperties;
import com.example.pcroom.domain.OrderStatus;
import com.example.pcroom.domain.Orders;
import com.example.pcroom.infrastructure.OrdersRepository;
import com.example.pcroom.infrastructure.kakao.KakaoPayClient;
import com.example.pcroom.presentation.kakao.KakaoOrderRequest;
import com.example.pcroom.presentation.kakao.KakaoOrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class PaymentRecoveryService {

    private final OrdersRepository ordersRepository;
    private final KakaoPayClient kakaoPayClient;
    private final KakaoPayProperties kakaoPayProperties;
    private final PaymentProcessService paymentProcessService;

    public PaymentRecoveryService(OrdersRepository ordersRepository, KakaoPayClient kakaoPayClient, KakaoPayProperties kakaoPayProperties, PaymentProcessService paymentProcessService) {
        this.ordersRepository = ordersRepository;
        this.kakaoPayClient = kakaoPayClient;
        this.kakaoPayProperties = kakaoPayProperties;
        this.paymentProcessService = paymentProcessService;
    }

    @Transactional
    public void recoverApprovingOrders() {
        List<Orders> ordersList =
                ordersRepository.findApprovingOrdersWithTid(OrderStatus.APPROVING);

        for (Orders orders : ordersList) {
            try {
                String tid = orders.getKakaoTid().getTid();

                // 카카오페이에 요청 보내는 로직
                KakaoOrderRequest request = KakaoOrderRequest.builder()
                        .cid(kakaoPayProperties.getCid())
                        .tid(tid)
                        .build();

                KakaoOrderResponse response = kakaoPayClient.order(request);

                // 응답 상태를 확인하여 approved 라면 결제 후 로직 처리
                if("PAYMENT".equals(response.getStatus())) {
                    paymentProcessService.processAfterApprove(orders);
                    log.info("[PaymentRecoveryService] order recoverd orderId = {}", orders.getId());
                } else if ("CANCEL".equals(response.getStatus())) {
                    orders.changeStatus(OrderStatus.CANCELED);
                } else {
                    log.info("[PaymentRecoveryService] still not approved orderId={}, status={}",
                            orders.getId(), response.getStatus());
                }
            } catch (Exception e) {
                log.error("[PaymentRecoveryService] recovery fail orderId={}", orders.getId(), e);
            }
        }
    }
}
