package com.example.pcroom.presentation.kakao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoOrderResponse {
    private String tid;
    private String cid;
    private String status;
    private String partnerOrderId;
    private String partnerUserId;
    private String approvedAt;
    private String canceledAt;
}
