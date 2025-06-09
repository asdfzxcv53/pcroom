package com.example.pcroom.domain;

import jakarta.persistence.*;

@Entity
public class OrdersProduct {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDERSPRODUCT_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDERS_ID")
    private Orders orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @Column(nullable = false)
    private int orderQuantity;

    @Column(nullable = false)
    private int orderPrice;
}
