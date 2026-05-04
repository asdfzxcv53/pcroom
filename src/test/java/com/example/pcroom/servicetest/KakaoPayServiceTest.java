package com.example.pcroom.servicetest;

import com.example.pcroom.application.KakaoPayService;
import com.example.pcroom.application.KakaoPayStateService;
import com.example.pcroom.application.PaymentProcessService;
import com.example.pcroom.domain.*;
import com.example.pcroom.domain.exception.KakaoPayCantCancelException;
import com.example.pcroom.domain.exception.KakaoPayFailException;
import com.example.pcroom.domain.exception.KakaoPayReadyException;
import com.example.pcroom.domain.exception.OrdersNotFoundException;
import com.example.pcroom.infrastructure.KakaoTidRepository;
import com.example.pcroom.infrastructure.OrdersRepository;
import com.example.pcroom.infrastructure.kakao.KakaoPayClient;
import com.example.pcroom.presentation.kakao.KakaoApproveRequest;
import com.example.pcroom.presentation.kakao.KakaoApproveResponse;
import com.example.pcroom.presentation.kakao.KakaoCancelResponse;
import com.example.pcroom.presentation.kakao.KakaoReadyResponse;
import com.example.pcroom.presentation.orders.OrdersCancelResponse;
import com.example.pcroom.presentation.orders.OrdersResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class KakaoPayServiceTest {

    @Mock
    private KakaoPayClient kakaoPayClient;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private KakaoTidRepository tidRepository;

    @Mock
    private KakaoPayProperties kakaoPayProperties;

    @Mock
    private KakaoPayStateService kakaoPayStateService;

    @Mock
    private PaymentProcessService paymentProcessService;

    @InjectMocks
    private KakaoPayService kakaoPayService;
// =========================
    // ready
    // =========================

    @Test
    @DisplayName("ready 성공 시 tid가 저장되고 응답을 반환한다")
    void ready_success_shouldSaveTidAndReturnResponse() {
        // given
        Long orderId = 1L;
        Orders orders = mock(Orders.class);
        User user = mock(User.class);

        OrdersResponseDto dto = mock(OrdersResponseDto.class);
        KakaoReadyResponse response = mock(KakaoReadyResponse.class);

        when(dto.getOrderId()).thenReturn(orderId);
        when(ordersRepository.findById(orderId)).thenReturn(Optional.of(orders));
        when(orders.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10L);
        when(orders.getId()).thenReturn(orderId);
        when(orders.getQuantity()).thenReturn(1);
        when(orders.getTotalPrice()).thenReturn(10000);

        when(kakaoPayProperties.getCid()).thenReturn("cid");
        when(kakaoPayClient.ready(any())).thenReturn(response);
        when(response.getTid()).thenReturn("tid");

        // when
        KakaoReadyResponse result = kakaoPayService.ready(dto);

        // then
        assertThat(result).isEqualTo(response);
        verify(tidRepository).save(any(KakaoTid.class));
    }

    @Test
    @DisplayName("ready 실패 시 예외 발생")
    void ready_fail_shouldThrowException() {
        // given
        Long orderId = 1L;
        OrdersResponseDto dto = mock(OrdersResponseDto.class);

        when(dto.getOrderId()).thenReturn(orderId);
        when(ordersRepository.findById(orderId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> kakaoPayService.ready(dto))
                .isInstanceOf(OrdersNotFoundException.class);
    }

    // =========================
    // approve
    // =========================

    @Test
    @DisplayName("approve 성공 시 후처리 로직이 실행된다")
    void approve_success_shouldProcessAfterApprove() {
        // given
        Long orderId = 1L;
        String pgToken = "pg_token";

        Orders orders = mock(Orders.class);
        KakaoTid kakaoTid = mock(KakaoTid.class);

        when(ordersRepository.findById(orderId)).thenReturn(Optional.of(orders));
        when(tidRepository.findByOrders(orders)).thenReturn(Optional.of(kakaoTid));

        when(kakaoTid.getTid()).thenReturn("tid");
        when(kakaoTid.getOrders()).thenReturn(orders);
        when(kakaoTid.getUserId()).thenReturn(1L);
        when(orders.getId()).thenReturn(orderId);

        when(kakaoPayProperties.getCid()).thenReturn("cid");
        when(kakaoPayClient.approve(any())).thenReturn(mock(KakaoApproveResponse.class));

        // when
        kakaoPayService.approve(pgToken, orderId);

        // then
        verify(kakaoPayStateService).markApproving(orderId);
        verify(paymentProcessService).processAfterApprove(orders);
    }

    @Test
    @DisplayName("approve 실패 시 주문 상태가 FAILED로 변경된다")
    void approve_fail_shouldChangeStatusToFailed() {
        // given
        Long orderId = 1L;
        String pgToken = "pg_token";

        Orders orders = mock(Orders.class);
        KakaoTid kakaoTid = mock(KakaoTid.class);

        when(ordersRepository.findById(orderId)).thenReturn(Optional.of(orders));
        when(tidRepository.findByOrders(orders)).thenReturn(Optional.of(kakaoTid));

        when(kakaoTid.getTid()).thenReturn("tid");
        when(kakaoTid.getOrders()).thenReturn(orders);
        when(kakaoTid.getUserId()).thenReturn(1L);

        when(kakaoPayProperties.getCid()).thenReturn("cid");
        when(kakaoPayClient.approve(any()))
                .thenThrow(new RestClientException("fail"));

        // when & then
        assertThatThrownBy(() -> kakaoPayService.approve(pgToken, orderId))
                .isInstanceOf(KakaoPayFailException.class);

        verify(orders).changeStatus(OrderStatus.FAILED);
    }

    // =========================
    // cancelBeforePay
    // =========================

    @Test
    @DisplayName("cancelBeforePay 성공 시 상태가 변경된다")
    void cancelBeforePay_success_shouldCallStateChange() {
        // given
        Long orderId = 1L;
        Orders orders = mock(Orders.class);

        when(ordersRepository.findById(orderId)).thenReturn(Optional.of(orders));

        // when
        kakaoPayService.cancelBeforePay(orderId);

        // then
        verify(kakaoPayStateService).markCancelBeforePay(orderId);
    }

    // =========================
    // cancelAfterPay
    // =========================

    @Test
    @DisplayName("cancelAfterPay 성공 시 응답을 반환한다")
    void cancelAfterPay_success_shouldReturnResponse() {
        // given
        Long orderId = 1L;

        Orders orders = mock(Orders.class);
        User user = mock(User.class);
        KakaoTid kakaoTid = mock(KakaoTid.class);

        when(ordersRepository.findById(orderId)).thenReturn(Optional.of(orders));

        when(orders.getId()).thenReturn(orderId);
        when(orders.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(orders.getTotalPrice()).thenReturn(10000);
        when(orders.getOrderTime()).thenReturn(LocalDateTime.now());

        when(orders.getKakaoTid()).thenReturn(kakaoTid);
        when(kakaoTid.getTid()).thenReturn("tid");

        when(kakaoPayProperties.getCid()).thenReturn("cid");

        when(kakaoPayClient.cancel(any()))
                .thenReturn(mock(KakaoCancelResponse.class));

        // when
        OrdersCancelResponse result = kakaoPayService.cancelAfterPay(orderId);

        // then
        assertThat(result.getOrderId()).isEqualTo(orderId);
        verify(kakaoPayStateService).markCancelAfterPay(orderId);
    }

    @Test
    @DisplayName("cancelAfterPay 실패 시 예외 발생")
    void cancelAfterPay_fail_shouldThrowException() {
        // given
        Long orderId = 1L;

        Orders orders = mock(Orders.class);
        KakaoTid kakaoTid = mock(KakaoTid.class);

        when(ordersRepository.findById(orderId)).thenReturn(Optional.of(orders));
        when(orders.getKakaoTid()).thenReturn(kakaoTid);
        when(kakaoTid.getTid()).thenReturn("tid");
        when(orders.getTotalPrice()).thenReturn(10000);

        when(kakaoPayProperties.getCid()).thenReturn("cid");

        when(kakaoPayClient.cancel(any()))
                .thenThrow(new RestClientException("fail"));

        // when & then
        assertThatThrownBy(() -> kakaoPayService.cancelAfterPay(orderId))
                .isInstanceOf(KakaoPayCantCancelException.class);
    }

    // =========================
    // fail
    // =========================

    @Test
    @DisplayName("fail 성공 시 상태가 FAIL로 변경된다")
    void fail_success_shouldCallStateChange() {
        // given
        Long orderId = 1L;
        Orders orders = mock(Orders.class);

        when(ordersRepository.findById(orderId)).thenReturn(Optional.of(orders));

        // when
        kakaoPayService.fail(orderId);

        // then
        verify(kakaoPayStateService).markFail(orderId);
    }
}
