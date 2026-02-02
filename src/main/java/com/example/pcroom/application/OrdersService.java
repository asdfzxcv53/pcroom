package com.example.pcroom.application;

import com.example.pcroom.domain.Orders;
import com.example.pcroom.domain.OrdersProduct;
import com.example.pcroom.domain.Product;
import com.example.pcroom.domain.User;
import com.example.pcroom.infrastructure.OrdersRepository;
import com.example.pcroom.infrastructure.ProductRepository;
import com.example.pcroom.infrastructure.UserRepository;
import com.example.pcroom.presentation.orders.OrdersProductRequestDto;
import com.example.pcroom.presentation.orders.OrdersProductResponseDto;
import com.example.pcroom.presentation.orders.OrdersRequestDto;
import com.example.pcroom.presentation.orders.OrdersResponseDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrdersService(OrdersRepository ordersRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.ordersRepository = ordersRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public OrdersResponseDto createOrder(OrdersRequestDto ordersRequestDto) {

        int totalPrice = 0;

        Orders orders = new Orders();
        orders.setOrderTime(LocalDateTime.now());
        User user = userRepository.findById(ordersRequestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        orders.setUser(user);

        List<OrdersProductRequestDto> ordersProductRequestDtos = ordersRequestDto.getOrdersProductRequestDtos(); // 주문목록을 추출

        for(OrdersProductRequestDto ordersProductRequestDto : ordersProductRequestDtos) {// 주문목록을 순회하며 주문상품 저장
            Product product = productRepository.findById(ordersProductRequestDto.getProductId());
            product.removeQuantity(ordersProductRequestDto.getProductQuantity()); // 상품 재고에서 수량만큼 빼기 ( 수량 부족하면 exception )
            totalPrice += product.getPrice() * ordersProductRequestDto.getProductQuantity(); // 총 가격 계산

            OrdersProduct ordersProduct = new OrdersProduct(orders, product, ordersProductRequestDto.getProductQuantity(), product.getPrice());
            orders.addOrdersProduct(ordersProduct); // cascade.ALL 로 orderProduct 는 order 과 같이 저장
        }
        orders.setTotalPrice(totalPrice);

        Orders savedOrders = ordersRepository.save(orders);
        OrdersResponseDto ordersResponseDto = new OrdersResponseDto();

        List<OrdersProductResponseDto> ordersProductResponseDtos = savedOrders.getOrdersProducts()
                .stream()
                .map(ordersProduct -> OrdersProductResponseDto.fromEntity(ordersProduct))
                .toList();
        // 나온 정보들을 다 합해서 응답Dto 생성

        ordersResponseDto.setOrdersProductResponseDtos(ordersProductResponseDtos);
        ordersResponseDto.setOrderId(savedOrders.getId());

        return ordersResponseDto;
    }

    public List<OrdersResponseDto> getOrdersByUserId(Long userId) {
        List<Orders> orders = ordersRepository.findOrdersByUserId(userId);

        List<OrdersResponseDto> ordersResponseDtos = new ArrayList<>();

        for(Orders order : orders) {
            OrdersResponseDto ordersResponseDto = new OrdersResponseDto();
            ordersResponseDto.setOrderId(order.getId());
            ordersResponseDto.setOrdersProductResponseDtos(
                    order.getOrdersProducts().stream()  // findOrdersByUserId 에서 join fetch 를 이용하여 OrdersProducts 동시에 select -> N + 1 문제 방지
                            .map(ordersProduct -> OrdersProductResponseDto.fromEntity(ordersProduct))
                            .toList()
            );

            ordersResponseDtos.add(ordersResponseDto);
        }

        return ordersResponseDtos;
    }
}
