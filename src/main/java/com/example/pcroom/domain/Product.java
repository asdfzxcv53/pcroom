package com.example.pcroom.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int quantity;

    @Builder
    public Product(String name, int price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Product() {

    }

    public void setId(Long id) {
        this.id = id;
    }
}
