package com.example.pcroom.application;

import com.example.pcroom.domain.OrderType;
import com.example.pcroom.domain.Orders;
import com.example.pcroom.domain.OutboxEvent;
import com.example.pcroom.domain.OutboxStatus;
import com.example.pcroom.domain.exception.OutboxPayloadSerializationException;
import com.example.pcroom.infrastructure.OutboxEventRepository;
import com.example.pcroom.presentation.orders.OrdersProductResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentProcessService {

    private final OutboxEventRepository outboxEventRepository;

    public PaymentProcessService(OutboxEventRepository outboxEventRepository){
        this.outboxEventRepository = outboxEventRepository;
    }

    @Transactional
    public void processAfterApprove(Orders orders) {

        // 이 주문이 시간 충전 이라면 1000원에 한시간으로 계산
        if(orders.getOrderType() == OrderType.TIME){
            long addSecond = (orders.getTotalPrice() / 1000) * 60 * 60;
            orders.getUser().getRemainTime().addRemainTime(addSecond);
        } else{

            try{
                List<OrdersProductResponseDto> ordersProductsDto =
                        orders.getOrdersProducts().stream()
                                .map(OrdersProductResponseDto::fromEntity)
                                .toList();

                ObjectMapper objectMapper = new ObjectMapper();

                // payload 로는 결제한 상품들 목록을 보내준다.
                String payload = objectMapper.writeValueAsString(ordersProductsDto);

                OutboxEvent outboxEvent =
                        new OutboxEvent(
                                OutboxStatus.PENDING,
                                payload,
                                LocalDateTime.now()
                        );

                // outbox에 이벤트 저장.
                // 결제 완료 이벤트를 관리자 pc에 보내준다.
                outboxEventRepository.save(outboxEvent);
            } catch (JsonProcessingException e) {
                throw new OutboxPayloadSerializationException("outbox payload serialization failed");
            }

        }
    }
}
