package com.example.pcroom.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Orders {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDERS_ID")
    private Long id;

    @Column(nullable = false)
    private int totalPrice;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private LocalDateTime orderTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
    private List<OrdersProduct> ordersProducts = new ArrayList<>();

    @OneToOne(mappedBy = "orders", cascade = CascadeType.ALL)
    private KakaoTid kakaoTid;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    public void addOrdersProduct(OrdersProduct ordersProduct) {
        ordersProducts.add(ordersProduct);
    }

    public void setUser (User user) {
        this.user = user;
        user.addOrders(this);
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setKakaoTid(KakaoTid kakaoTid) {this.kakaoTid = kakaoTid;}

    public void setOrderType(OrderType orderType) {this.orderType = orderType;}

    public void changeStatus(OrderStatus status) {
        this.status = status;
    }
}
