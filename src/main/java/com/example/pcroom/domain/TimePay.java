package com.example.pcroom.domain;

import jakarta.persistence.*;

@Entity
public class TimePay {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TIMEPAY_ID")
    private Long id;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private Long addTime; // 초단위로 저장

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;
}
