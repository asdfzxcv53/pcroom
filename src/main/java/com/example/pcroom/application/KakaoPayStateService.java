package com.example.pcroom.application;

import com.example.pcroom.domain.OrderStatus;
import com.example.pcroom.domain.exception.KakaoPayCantCancelException;
import com.example.pcroom.domain.exception.KakaoPayFailException;
import com.example.pcroom.infrastructure.OrdersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
@Slf4j
public class KakaoPayStateService {

    private final OrdersRepository ordersRepository;

    public KakaoPayStateService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @Transactional(propagation = REQUIRES_NEW)
    public void markApproving(Long orderId) {
        // PENDING 상태 확인
        int updated = ordersRepository.updateStatusIfPending(
                orderId,
                OrderStatus.APPROVING
        );
        // 상태를 atomic 하게 update 해줘서 race condition 해결
        // 결과가 1이면 변경, 0이면 에러
        if (updated == 0) {
            log.warn("[KakaoPay] approve failed because order not pending orderId={}", orderId);
            throw new KakaoPayFailException("order not pending");
        }
    }

    @Transactional
    public void markCancel(Long orderId) {
        // PENDING 상태 확인
        int updated = ordersRepository.updateStatusIfPending(
                orderId,
                OrderStatus.CANCELED
        );

        // 여기도 마찬가지로 atomic 하게 update 해준다.
        if (updated == 0) {
            log.warn("[KakaoPay] cancel failed because order not pending ordersId = {}", orderId);
            throw new KakaoPayCantCancelException("order already processed");
        }
    }
}
