package com.example.pcroom.presentation.kakao;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoOrderRequest {
    private String cid;
    private String tid;
}
