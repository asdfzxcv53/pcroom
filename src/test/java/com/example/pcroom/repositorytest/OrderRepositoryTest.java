package com.example.pcroom.repositorytest;

import com.example.pcroom.domain.Orders;
import com.example.pcroom.domain.OrdersProduct;
import com.example.pcroom.domain.Product;
import com.example.pcroom.domain.User;
import com.example.pcroom.infrastructure.OrdersRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class OrderRepositoryTest {

    @Autowired
    private OrdersRepository ordersRepository;

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("주문 저장")
    public void saveOrders() throws Exception {

        // Given

        User user = new User("sskij", "1234", "seungwoo", "01082112923");

        Product product1 = new Product("abc", 1000, 100);
        Product product2 = new Product("def", 2000, 200);

        Orders orders = new Orders();

        OrdersProduct ordersProduct1 = new OrdersProduct(orders, product1, 10, 1000);
        OrdersProduct ordersProduct2 = new OrdersProduct(orders, product2, 10, 2000);

        orders.setOrderTime(LocalDateTime.now());
        orders.setUser(user);
        orders.setTotalPrice(30000);
        orders.addOrdersProduct(ordersProduct1);
        orders.addOrdersProduct(ordersProduct2);

        // When

        Orders savedOrders = ordersRepository.save(orders);

        // Then

        assertThat(savedOrders.getOrdersProducts().get(0).getProduct()).isEqualTo(product1);
        assertThat(savedOrders.getOrdersProducts().get(1).getProduct()).isEqualTo(product2);
        assertThat(savedOrders.getOrdersProducts().get(0).getOrderPrice()).isEqualTo(1000);
        assertThat(savedOrders.getOrdersProducts().get(1).getOrderPrice()).isEqualTo(2000);
        assertThat(savedOrders.getOrdersProducts().get(0).getOrderQuantity()).isEqualTo(10);
        assertThat(savedOrders.getOrdersProducts().get(1).getOrderQuantity()).isEqualTo(10);

        assertThat(savedOrders.getTotalPrice()).isEqualTo(30000);

    }

}
