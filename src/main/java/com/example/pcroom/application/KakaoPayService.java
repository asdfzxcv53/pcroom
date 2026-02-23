package com.example.pcroom.application;

import com.example.pcroom.domain.*;
import com.example.pcroom.domain.exception.*;
import com.example.pcroom.infrastructure.KakaoTidRepository;
import com.example.pcroom.infrastructure.OrdersRepository;
import com.example.pcroom.presentation.kakao.*;
import com.example.pcroom.presentation.orders.OrdersCancelResponse;
import com.example.pcroom.presentation.orders.OrdersResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Locale;

@Service
@Transactional
@Slf4j
public class KakaoPayService {

    private final KakaoTidRepository tidRepository;
    private final KakaoPayProperties kakaoPayProperties;
    private final OrdersRepository ordersRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String READY_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";
    private static final String APPROVE_URL = "https://open-api.kakaopay.com/online/v1/payment/approve";
    private static final String CANCEL_URL = "https://open-api.kakaopay.com/online/v1/payment/cancel";

    @Autowired
    public KakaoPayService(KakaoTidRepository tidRepository, KakaoPayProperties kakaoPayProperties, OrdersRepository ordersRepository, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.tidRepository = tidRepository;
        this.kakaoPayProperties = kakaoPayProperties;
        this.ordersRepository = ordersRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    // 실제 대기상태인 주문을 만든 후에 카카오페이에 ready api 를 보낸다.
    public KakaoReadyResponse ready(OrdersResponseDto ordersResponseDto) {
        log.info("[KakaoPay] ready start ordersId = {}",
                ordersResponseDto.getOrderId());

        Orders orders = ordersRepository.findById(ordersResponseDto.getOrderId())
                .orElseThrow(() -> {
                    log.warn("[KakaoPay] ready orders not found ordersId = {}",
                            ordersResponseDto.getOrderId());
                    return new OrdersNotFoundException("orders not found");
                });

        HttpHeaders headers = getHeaders();

        KakaoReadyRequest kakaoReadyRequest = KakaoReadyRequest.builder()
                .cid(kakaoPayProperties.getCid())
                .partner_order_id("" + orders.getId())
                .partner_user_id("" + orders.getUser().getId())
                .item_name("test_product")
                .quantity(orders.getQuantity())
                .total_amount(orders.getTotalPrice())
                .tax_free_amount(0)
                .approval_url("http://localhost:8080/pay/success?orderId=" + orders.getId())
                .cancel_url("http://localhost:8080/pay/cancel?orderId=" + orders.getId())
                .fail_url("http://localhost:8080/pay/fail?orderId=" + orders.getId())
                .build();

        HttpEntity<KakaoReadyRequest> entity =
                new HttpEntity<>(kakaoReadyRequest, headers);
        try {
            HttpEntity<KakaoReadyResponse> response =
                    restTemplate.postForEntity(
                            READY_URL,
                            entity,
                            KakaoReadyResponse.class
                    );

            if(response.getBody() == null) {
                log.warn("[KakaoPay] ready response null orderId={}",
                        orders.getId());
                throw new KakaoPayReadyException("kakao pay ready response null");
            }
            KakaoTid kakaoTid = new KakaoTid(response.getBody().getTid(), orders, orders.getUser().getId());

            tidRepository.save(kakaoTid);

            log.info("[KakaoPay] ready success orderId={}", orders.getId());

            return response.getBody();

        } catch (RestClientException e) {
            log.warn("[KakaoPay] ready response fail ordersId = {}",
                    orders.getId());
            throw new KakaoPayReadyException("kakao pay ready api fail");
        }
    }

    // ready 이후 받은 토큰으로 approve 요청
    public KakaoApproveResponse approve(String pg_token, Long orderId) {
        log.info("[KakaoPay] approve start ordersId = {}",
                orderId);

        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("[KakaoPay] approve orders not found ordersId = {}",
                            orderId);
                    return new OrdersNotFoundException("orders not found");
                });

        KakaoTid kakaoTid = tidRepository.findByOrders(orders)
                .orElseThrow(() -> {
                    log.warn("[KakaoPay] approve kakaoTid not found ordersId = {}",
                            orderId);
                    return new KakaoTidNotFoundException("kakaoTid not found");
                });

        String tid = kakaoTid.getTid();

        HttpHeaders headers = getHeaders();

        KakaoApproveRequest kakaoApproveRequest = KakaoApproveRequest.builder()
                .cid(kakaoPayProperties.getCid())
                .tid(tid)
                .partner_order_id("" +kakaoTid.getOrders().getId())
                .partner_user_id("" +kakaoTid.getUserId())
                .pg_token(pg_token)
                .build();

        HttpEntity<KakaoApproveRequest> entity =
                new HttpEntity<>(kakaoApproveRequest, headers);

        // 먼저 카카오페이 에 approve api 를 보내 응답이 제대로 오면 우리 서버의 결제를 승인시킨다.
        try {
            HttpEntity<KakaoApproveResponse> response =
                    restTemplate.postForEntity(
                            APPROVE_URL,
                            entity,
                            KakaoApproveResponse.class
                    );

            log.info("[KakaoPay] approve success orderId={}",
                    orders.getId());

            orders.changeStatus(OrderStatus.PAID);
            // 이 시점에 주문이 들어간다.

            // 이 주문이 시간 충전 이라면 1000원에 한시간으로 계산
            if(orders.getOrderType() == OrderType.TIME){
                long addSecond = (orders.getTotalPrice() / 1000) * 60 * 60;
                orders.getUser().getRemainTime().addRemainTime(addSecond);
            }

            return response.getBody();
        } catch (RestClientException e) {
            log.error("[KakaoPay] approve fail orderId={}",
                    orderId, e);
            orders.changeStatus(OrderStatus.FAILED);
            throw new KakaoPayFailException("kakao pay fail");
        }
    }

    // approve 전에 카카오페이 서버에서 cancel url 로 redirect 한 경우
    public void cancelBeforePay(Long orderId) {
        log.info("[KakaoPay] cancel before pay start ordersId = {}",
                orderId);

        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("[KakaoPay] cancel before pay orders not found ordersId = {}",
                            orderId);
                    return new OrdersNotFoundException("orders not found");
                });

        if(orders.getStatus() != OrderStatus.PENDING) {
            log.warn("[KakaoPay] cancel before pay orders cant cancel because not pending ordersId = {}",
                    orderId);
            throw new KakaoPayCantCancelException("order is not pending");
        }
        orders.changeStatus(OrderStatus.CANCELED);
        // 아직 결제가 이루어지지 않아서 order 의 상태만 바꿔준다
        log.info("[KakaoPay] cancel before pay success ordersId = {}",
                orderId);
    }

    // approve 후에 사용자가 환불을 요구한 경우
    // 전체 취소만 구현
    public OrdersCancelResponse cancelAfterPay(Long orderId) {
        log.info("[KakaoPay] cancel after pay start ordersId = {}",
                orderId);

        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("[KakaoPay] cancel after pay orders not found ordersId = {}",
                            orderId);
                    return new OrdersNotFoundException("orders not found");
                });

        if(orders.getStatus() != OrderStatus.PAID){
            log.warn("[KakaoPay] cancel after pay orders cant cancel because not paid ordersId = {}",
                    orderId);
            throw new KakaoPayCantCancelException("order is not paid");
        }

        // 이미 결제가 이루어진 상황이므로 카카오페이에 cancel api 를 보낸다.
        // 카카오페이에 cancel api 를 보내고 확인 응답을 받고 우리 서비스의 취소내역을 기록한다.
        KakaoCancelRequest kakaoCancelRequest = new KakaoCancelRequest(
                kakaoPayProperties.getCid(),
                orders.getKakaoTid().getTid(),
                orders.getTotalPrice(),
                0
        );

        HttpHeaders headers = getHeaders();

        HttpEntity<KakaoCancelRequest> entity =
                new HttpEntity<>(kakaoCancelRequest, headers);

        try {

            HttpEntity<KakaoCancelResponse> response =
                    restTemplate.postForEntity(
                            CANCEL_URL,
                            entity,
                            KakaoCancelResponse.class
                    );

            log.info("[KakaoPay] cancel after pay success orderId={}", orders.getId());

            // response 가 제대로 온 경우 -> 카카오페이에서 환불이 이루어진 상황
            // 이시점에 우리 서비스의 결제 취소를 업데이트 해준다.
            orders.changeStatus(OrderStatus.CANCELED);

        } catch (HttpStatusCodeException e) {

            String body = e.getResponseBodyAsString();

            try {
                KakaoErrorResponse error =
                        objectMapper.readValue(body, KakaoErrorResponse.class);

                log.warn("[KakaoPay] cancel after pay kakao server error ordersId = {}",
                        orderId);
                throw new KakaoPayCantCancelException(error.getMessage());
            } catch (IOException e1) {
                log.warn("[KakaoPay] cancel after pay response parsing error orderId = {}",
                        orderId);
                throw new KakaoPayCantCancelException("kakao response parsing error");
            }
        }

        // 취소한 금액 등 응답을 return 해준다.
        // 현재는 모든 금액을 환불하는 로직만 구성.
        OrdersCancelResponse cancelResponse = new OrdersCancelResponse(
                orders.getId(),
                orders.getUser().getId(),
                orders.getTotalPrice(),
                orders.getOrderTime()
        );

        return cancelResponse;
    }

    // fail redirect 가 오는 경우 아직 결제를 하지 않은 상태
    // 우리 서비스의 주문 상태만 변경해준다.
    public void fail(Long orderId){
        log.info("[KakaoPay] fail start ordersId = {}",
                orderId);

        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("[KakaoPay] fail orders not found ordersId = {}",
                            orderId);
                    return new OrdersNotFoundException("orders not found");
                });

        if(orders.getStatus() == OrderStatus.PAID){
            log.warn("[KakaoPay] fail order cant fail beacause already paid ordersId = {}",
                    orderId);
            throw new KakaoPayCantFailException("order is already paid");
        }

        orders.changeStatus(OrderStatus.FAILED);
        log.info("[KakaoPay] fail success ordersId = {}",
                orderId);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + kakaoPayProperties.getSecretKey());

        return headers;
    }
}

