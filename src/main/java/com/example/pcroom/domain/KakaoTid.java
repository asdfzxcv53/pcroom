package com.example.pcroom.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class KakaoTid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tid;

    @OneToOne
    @JoinColumn(name = "orders_id", nullable = false, unique = true)
    private Orders orders;

    @Column(nullable = false)
    private Long userId;

    public KakaoTid() {}
    public KakaoTid(String tid, Orders orders,Long userId) {
        this.tid = tid;
        this.orders = orders;
        this.userId = userId;
    }
}
