package com.example.pcroom.application;

import com.example.pcroom.domain.*;
import com.example.pcroom.domain.exception.*;
import com.example.pcroom.infrastructure.KakaoTidRepository;
import com.example.pcroom.infrastructure.OrdersRepository;
import com.example.pcroom.infrastructure.kakao.KakaoPayClient;
import com.example.pcroom.presentation.kakao.*;
import com.example.pcroom.presentation.orders.OrdersCancelResponse;
import com.example.pcroom.presentation.orders.OrdersResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

@Service
@Transactional
@Slf4j
public class KakaoPayService {

    private final KakaoPayStateService kakaoPayStateService;
    private final KakaoTidRepository tidRepository;
    private final PaymentProcessService paymentProcessService;
    private final KakaoPayProperties kakaoPayProperties;
    private final OrdersRepository ordersRepository;
    private final ObjectMapper objectMapper;
    private final KakaoPayClient kakaoPayClient;

    @Autowired
    public KakaoPayService(KakaoPayStateService kakaoPayStateService, KakaoTidRepository tidRepository, PaymentProcessService paymentProcessService, KakaoPayProperties kakaoPayProperties, OrdersRepository ordersRepository, KakaoPayClient kakaoPayClient, ObjectMapper objectMapper) {
        this.kakaoPayStateService = kakaoPayStateService;
        this.tidRepository = tidRepository;
        this.paymentProcessService = paymentProcessService;
        this.kakaoPayProperties = kakaoPayProperties;
        this.ordersRepository = ordersRepository;
        this.objectMapper = objectMapper;
        this.kakaoPayClient = kakaoPayClient;
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

        try {
            KakaoReadyResponse response = kakaoPayClient.ready(kakaoReadyRequest);

            if(response == null) {
                log.warn("[KakaoPay] ready response null orderId={}",
                        orders.getId());
                throw new KakaoPayReadyException("kakao pay ready response null");
            }
            KakaoTid kakaoTid = new KakaoTid(response.getTid(), orders, orders.getUser().getId());

            tidRepository.save(kakaoTid);

            log.info("[KakaoPay] ready success orderId={}", orders.getId());

            return response;

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

        // 상태를 원자적으로 approving 으로 변경
        kakaoPayStateService.markApproving(orderId);

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

        KakaoApproveRequest kakaoApproveRequest = KakaoApproveRequest.builder()
                .cid(kakaoPayProperties.getCid())
                .tid(tid)
                .partner_order_id("" +kakaoTid.getOrders().getId())
                .partner_user_id("" +kakaoTid.getUserId())
                .pg_token(pg_token)
                .build();

        // 먼저 카카오페이 에 approve api 를 보내 응답이 제대로 오면 우리 서버의 결제를 승인시킨다.
        try {
            KakaoApproveResponse response = kakaoPayClient.approve(kakaoApproveRequest);

            log.info("[KakaoPay] approve success orderId={}",
                    orders.getId());

            // 외부 Service 에서 결제 승인 후처리 작업 진행
            paymentProcessService.processAfterApprove(orders);

            return response;
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

       ordersRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("[KakaoPay] cancel before pay orders not found ordersId = {}",
                            orderId);
                    return new OrdersNotFoundException("orders not found");
                });

        // 상태를 원자적으로 cancel 로 변경
        kakaoPayStateService.markCancel(orderId);

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

        try {
            KakaoCancelResponse response = kakaoPayClient.cancel(kakaoCancelRequest);

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
}

