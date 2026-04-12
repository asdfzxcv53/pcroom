package com.example.pcroom.infrastructure.kakao;

import com.example.pcroom.domain.KakaoPayProperties;
import com.example.pcroom.presentation.kakao.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoPayClient {

    private static final String READY_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";
    private static final String APPROVE_URL = "https://open-api.kakaopay.com/online/v1/payment/approve";
    private static final String CANCEL_URL = "https://open-api.kakaopay.com/online/v1/payment/cancel";
    private static final String ORDER_URL = "https://open-api.kakaopay.com/online/v1/payment/order";

    private final RestTemplate restTemplate;
    private final KakaoPayProperties kakaoPayProperties;

    public KakaoPayClient(RestTemplate restTemplate, KakaoPayProperties kakaoPayProperties) {
        this.restTemplate = restTemplate;
        this.kakaoPayProperties = kakaoPayProperties;
    }

    public KakaoReadyResponse ready(KakaoReadyRequest request) {
        HttpEntity<KakaoReadyRequest> entity =
                new HttpEntity<>(request, createJsonHeaders());

        return restTemplate.postForEntity(
                READY_URL,
                entity,
                KakaoReadyResponse.class
        ).getBody();
    }

    public KakaoApproveResponse approve(KakaoApproveRequest request) {
        HttpEntity<KakaoApproveRequest> entity =
                new HttpEntity<>(request, createJsonHeaders());

        return restTemplate.postForEntity(
                APPROVE_URL,
                entity,
                KakaoApproveResponse.class
        ).getBody();
    }

    public KakaoCancelResponse cancel(KakaoCancelRequest request) {
        HttpEntity<KakaoCancelRequest> entity =
                new HttpEntity<>(request, createJsonHeaders());

        return restTemplate.postForEntity(
                CANCEL_URL,
                entity,
                KakaoCancelResponse.class
        ).getBody();
    }

    public KakaoOrderResponse order(KakaoOrderRequest request) {
        HttpEntity<KakaoOrderRequest> entity =
                new HttpEntity<>(request, createJsonHeaders());

        return restTemplate.postForEntity(
                ORDER_URL,
                entity,
                KakaoOrderResponse.class
        ).getBody();
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + kakaoPayProperties.getSecretKey());
        return headers;
    }
}
