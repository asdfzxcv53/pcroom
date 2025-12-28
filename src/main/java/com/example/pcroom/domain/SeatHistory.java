package com.example.pcroom.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class SeatHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEAT_ID")
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = true)
    private LocalDateTime endTime;

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public SeatHistory() {}
    public SeatHistory(Seat seat, User user, LocalDateTime startTime, LocalDateTime endTime) {
        this.seat = seat;
        this.user = user;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
