package com.example.pcroom.domain;

import com.example.pcroom.presentation.orders.OrdersResponseDto;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status = OutboxStatus.PENDING;

    @Getter
    @Column(nullable = false)
    private String payload;     // 주문 정보

    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

    public OutboxEvent() {}
    public OutboxEvent(OutboxStatus status, String payload, LocalDateTime createdAt) {
        this.status = status;
        this.payload = payload;
        this.createdAt = createdAt;
    }

    public void markSent() {
        this.status = OutboxStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }
}
