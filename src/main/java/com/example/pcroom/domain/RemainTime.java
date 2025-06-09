package com.example.pcroom.domain;

import jakarta.persistence.*;

@Entity
public class RemainTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long remainTime;
}
