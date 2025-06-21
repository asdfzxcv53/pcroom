package com.example.pcroom.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Seat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEAT_ID")
    private Long id;

    @Column(nullable = false)
    private int seatNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatStatus seatStatus;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL)
    private List<SeatHistory> seatHistory = new ArrayList<SeatHistory>();

    public void addSeatHistory(SeatHistory seatHistory) {
        this.seatHistory.add(seatHistory);
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public void setSeatStatus(SeatStatus seatStatus) {
        this.seatStatus = seatStatus;
    }
}
