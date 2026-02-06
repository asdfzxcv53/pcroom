package com.example.pcroom.presentation.kakao;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class KakaoCancelResponse {
    private String aid;
    private String tid;
    private String cid;
    private String status;

    private String partnerOrderId;
    private String partnerUserId;

    private String paymentMethodType;

    private Amount amount;
    private Amount approvedCancelAmount;
    private Amount canceledAmount;
    private Amount cancelAvailableAmount;

    private String itemName;
    private String itemCode;
    private Integer quantity;

    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private LocalDateTime canceledAt;

    private String payload;
}
