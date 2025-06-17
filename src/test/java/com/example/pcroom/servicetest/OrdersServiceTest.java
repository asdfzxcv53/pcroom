package com.example.pcroom.servicetest;

import com.example.pcroom.application.OrdersService;
import com.example.pcroom.domain.Orders;
import com.example.pcroom.domain.Product;
import com.example.pcroom.domain.User;
import com.example.pcroom.domain.exception.NotEnoughStockException;
import com.example.pcroom.infrastructure.OrdersRepository;
import com.example.pcroom.infrastructure.ProductRepository;
import com.example.pcroom.infrastructure.UserRepository;
import com.example.pcroom.presentation.orders.OrdersProductRequestDto;
import com.example.pcroom.presentation.orders.OrdersProductResponseDto;
import com.example.pcroom.presentation.orders.OrdersRequestDto;
import com.example.pcroom.presentation.orders.OrdersResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrdersServiceTest {

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrdersService ordersService;

    @Test
    @DisplayName("상품 주문")
    public void createOrders() throws Exception{

        // Given

        User user = new User();

        Product product1 = Product.builder()
                .name("abc")
                .price(1000)
                .quantity(100)
                .build();

        Product product2 = Product.builder()
                .name("def")
                .price(2000)
                .quantity(20)
                .build();

        OrdersProductRequestDto oprd1 = new OrdersProductRequestDto();
        oprd1.setProductId(1L);
        oprd1.setProductQuantity(5);

        OrdersProductRequestDto oprd2 = new OrdersProductRequestDto();
        oprd2.setProductId(2L);
        oprd2.setProductQuantity(10);

        List<OrdersProductRequestDto> ordersProductRequestDtos = List.of(oprd1, oprd2);

        OrdersRequestDto ordersRequestDto = new OrdersRequestDto();
        ordersRequestDto.setUserId(1L);
        ordersRequestDto.setOrdersProductRequestDtos(ordersProductRequestDtos);

        Orders orders = new Orders();

        // WHen

        when(userRepository.findById(1L)).thenReturn(user);
        when(productRepository.findById(1L)).thenReturn(product1);
        when(productRepository.findById(2L)).thenReturn(product2);
        when(ordersRepository.save(any())).thenReturn(orders);

        OrdersResponseDto result = ordersService.createOrder(ordersRequestDto);

        // Then

        assertThat(result.getOrdersProductResponseDtos())
                .hasSize(2)
                .extracting("productId", "productName", "productPrice", "productQuantity")
                .containsExactly(
                        tuple(null, "abc", 1000, 5),
                        tuple(null, "def", 2000, 10)
                );


    }
    @Test
    @DisplayName("재고 부족")
    public void notEnoughQuantity() throws Exception{

        // Given

        User user = new User();

        Product product1 = Product.builder()
                .name("abc")
                .price(1000)
                .quantity(100)
                .build();

        Product product2 = Product.builder()
                .name("def")
                .price(2000)
                .quantity(20)
                .build();

        OrdersProductRequestDto oprd1 = new OrdersProductRequestDto();
        oprd1.setProductId(1L);
        oprd1.setProductQuantity(5);

        OrdersProductRequestDto oprd2 = new OrdersProductRequestDto();
        oprd2.setProductId(2L);
        oprd2.setProductQuantity(100);

        List<OrdersProductRequestDto> ordersProductRequestDtos = List.of(oprd1, oprd2);

        OrdersRequestDto ordersRequestDto = new OrdersRequestDto();
        ordersRequestDto.setUserId(1L);
        ordersRequestDto.setOrdersProductRequestDtos(ordersProductRequestDtos);

        Orders orders = new Orders();

        // WHen

        when(userRepository.findById(1L)).thenReturn(user);
        when(productRepository.findById(1L)).thenReturn(product1);
        when(productRepository.findById(2L)).thenReturn(product2);
        //when(ordersRepository.save(any())).thenReturn(orders);

        // Then

        Assertions.assertThrows(NotEnoughStockException.class, () -> {
            ordersService.createOrder(ordersRequestDto);
        });
    }
}
