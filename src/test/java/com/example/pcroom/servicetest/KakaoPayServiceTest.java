package com.example.pcroom.servicetest;

import com.example.pcroom.application.KakaoPayService;
import com.example.pcroom.domain.*;
import com.example.pcroom.domain.exception.KakaoPayReadyException;
import com.example.pcroom.infrastructure.KakaoTidRepository;
import com.example.pcroom.infrastructure.OrdersRepository;
import com.example.pcroom.presentation.kakao.KakaoReadyResponse;
import com.example.pcroom.presentation.orders.OrdersResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KakaoPayServiceTest {

    @InjectMocks
    private KakaoPayService kakaoPayService;

    @Mock
    private KakaoTidRepository kakaoTidRepository;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private KakaoPayProperties kakaoPayProperties;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("ready 요청 성공")
    public void ready_success() {
        //Given

        Long orderId = 1L;
        OrdersResponseDto ordersResponseDto = new OrdersResponseDto();
        ordersResponseDto.setOrderId(orderId);

        KakaoReadyResponse kakaoReadyResponse = new KakaoReadyResponse();
        kakaoReadyResponse.setTid("tid");

        ResponseEntity<KakaoReadyResponse> responseEntity =
                ResponseEntity.ok(kakaoReadyResponse);

        Orders orders = new Orders();
        User user = new User("username", "password", "name", "010", Role.USER);
        orders.setUser(user);
        orders.setOrderTime(LocalDateTime.now());
        orders.setTotalPrice(10000);
        orders.setQuantity(2);

        when(ordersRepository.findById(orderId)).thenReturn(Optional.of(orders));
        when(kakaoPayProperties.getCid()).thenReturn("cid");
        when(restTemplate.postForEntity(
                anyString(),
                any(),
                eq(KakaoReadyResponse.class)
        )).thenReturn(responseEntity);

        //When
        KakaoReadyResponse result = kakaoPayService.ready(ordersResponseDto);

        //Then
        assertThat(result.getTid()).isEqualTo("tid");
        verify(kakaoTidRepository, times(1)).save(any(KakaoTid.class));

    }

    @Test
    @DisplayName("ready 요청 후 body null error")
    public void ready_fail_body_null_error() {
        //Given

        Long orderId = 1L;
        OrdersResponseDto ordersResponseDto = new OrdersResponseDto();
        ordersResponseDto.setOrderId(orderId);

        KakaoReadyResponse kakaoReadyResponse = null;

        ResponseEntity<KakaoReadyResponse> responseEntity =
                ResponseEntity.ok(kakaoReadyResponse);

        Orders orders = new Orders();
        User user = new User("username", "password", "name", "010", Role.USER);
        orders.setUser(user);
        orders.setOrderTime(LocalDateTime.now());
        orders.setTotalPrice(10000);
        orders.setQuantity(2);

        when(ordersRepository.findById(orderId)).thenReturn(Optional.of(orders));
        when(kakaoPayProperties.getCid()).thenReturn("cid");
        when(restTemplate.postForEntity(
                anyString(),
                any(),
                eq(KakaoReadyResponse.class)
        )).thenReturn(responseEntity);

        //When
        assertThrows(
                KakaoPayReadyException.class,
                () -> kakaoPayService.ready(ordersResponseDto)
        );
    }

    @Test
    @DisplayName("결제 이후 cancel 요청 성공")
}
