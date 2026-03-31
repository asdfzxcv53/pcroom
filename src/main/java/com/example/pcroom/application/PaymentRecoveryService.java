package com.example.pcroom.application;

import com.example.pcroom.domain.OrderStatus;
import com.example.pcroom.domain.Orders;
import com.example.pcroom.infrastructure.OrdersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PaymentRecoveryService {

    private final OrdersRepository ordersRepository;

    public PaymentRecoveryService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    public void recoverApprovingOrders() {
        List<Orders> ordersList =
                ordersRepository.findApprovingOrdersWithTid(OrderStatus.APPROVING);

        for (Orders orders : ordersList) {
            try {
                String tid = orders.getKakaoTid().getTid();

                // 카카오페이에 요청 보내는 로직

                // 응답 상태를 확인하여 approved 라면 결제 후 로직 처리

            } catch (Exception e) {
                log.error("[PaymentRecoveryService] recovery fail orderId={}", orders.getId(), e);
            }
        }
    }
}
