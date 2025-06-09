package com.example.pcroom.domain;

import jakarta.persistence.*;

@Entity
public class Seat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEAT_ID")
    private Long id;

    @Column(nullable = false)
    private int seatNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatStatus seatStatus;
}
