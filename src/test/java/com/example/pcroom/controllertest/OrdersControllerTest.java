package com.example.pcroom.controllertest;

import com.example.pcroom.application.OrdersService;
import com.example.pcroom.presentation.controller.OrdersController;
import com.example.pcroom.presentation.orders.OrdersProductRequestDto;
import com.example.pcroom.presentation.orders.OrdersProductResponseDto;
import com.example.pcroom.presentation.orders.OrdersRequestDto;
import com.example.pcroom.presentation.orders.OrdersResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = OrdersController.class)
public class OrdersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrdersService ordersService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrdersProductRequestDto oprd1;
    private OrdersProductRequestDto oprd2;

    private OrdersProductResponseDto ordersProductResponseDto1;
    private OrdersProductResponseDto ordersProductResponseDto2;

    @BeforeEach
    void setUp() {
        oprd1 = new OrdersProductRequestDto();
        oprd1.setProductId(1L);
        oprd1.setProductQuantity(5);

        oprd2 = new OrdersProductRequestDto();
        oprd2.setProductId(2L);
        oprd2.setProductQuantity(10);

        ordersProductResponseDto1 = new OrdersProductResponseDto();
        ordersProductResponseDto1.setProductId(1L);
        ordersProductResponseDto1.setProductName("abc");
        ordersProductResponseDto1.setProductQuantity(5);
        ordersProductResponseDto1.setProductPrice(1000);

        ordersProductResponseDto2 = new OrdersProductResponseDto();
        ordersProductResponseDto2.setProductId(2L);
        ordersProductResponseDto2.setProductName("def");
        ordersProductResponseDto2.setProductQuantity(10);
        ordersProductResponseDto2.setProductPrice(2000);
    }

    @Test
    @DisplayName("주문 저장 api test")
    public void ordersSaveTest() throws Exception {

        // Given

        List<OrdersProductRequestDto> ordersProductRequestDtos = List.of(oprd1, oprd2);

        OrdersRequestDto ordersRequestDto = new OrdersRequestDto();
        ordersRequestDto.setUserId(1L);
        ordersRequestDto.setOrdersProductRequestDtos(ordersProductRequestDtos);

        OrdersResponseDto ordersResponseDto = new OrdersResponseDto();
        ordersResponseDto.setOrdersProductResponseDtos(List.of(ordersProductResponseDto1, ordersProductResponseDto2));

        // When

        when(ordersService.createOrder(any(OrdersRequestDto.class))).thenReturn(ordersResponseDto);

        // Then

        mockMvc.perform(post("/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(ordersRequestDto)))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.ordersProductResponseDtos[0].productId").value(1L))
                .andExpect(jsonPath("$.ordersProductResponseDtos[0].productName").value("abc"))
                .andExpect(jsonPath("$.ordersProductResponseDtos[0].productQuantity").value(5))
                .andExpect(jsonPath("$.ordersProductResponseDtos[0].productPrice").value(1000))
                //.andExpect(jsonPath("$.ordersProductResponseDtos[1].productId").value(2L))
                .andExpect(jsonPath("$.ordersProductResponseDtos[1].productName").value("def"))
                .andExpect(jsonPath("$.ordersProductResponseDtos[1].productQuantity").value(10))
                .andExpect(jsonPath("$.ordersProductResponseDtos[1].productPrice").value(2000));
    }

}
