package com.example.pcroom.domain;

import jakarta.persistence.*;

@Entity
public class RemainTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long remainTime; // second 단위

    @OneToOne
    @JoinColumn(name = "USER_ID", nullable = false, unique = true)
    private User user;

    public void addRemainTime(Long time) {
        this.remainTime += time;
    }

    public RemainTime() {}
    public RemainTime(User user) {
        this.user = user;
        this.remainTime = 0L;
    }
}
