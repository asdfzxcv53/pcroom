package com.example.pcroom.application.scheduler;

import com.example.pcroom.application.PaymentRecoveryService;
import com.example.pcroom.domain.OrderStatus;
import com.example.pcroom.domain.Orders;
import com.example.pcroom.infrastructure.OrdersRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class KakaoPaySchedulerService {

    private final PaymentRecoveryService paymentRecoveryService;

    public KakaoPaySchedulerService(PaymentRecoveryService paymentRecoveryService) {
        this.paymentRecoveryService = paymentRecoveryService;
    }

    @Scheduled(fixedRate = 3000)
    public void recoverApprovingOrders() {
        log.info("[PaymentRecoveryScheduler] recovery start");
        paymentRecoveryService.recoverApprovingOrders();
        log.info("[PaymentRecoveryScheduler] recovery end");
    }
}
