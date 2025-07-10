package com.example.pcroom.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
public class RemainTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long remainTime; // second 단위

    @Column
    private LocalDateTime endTime; // user 가 로그인하면 생성되고 null 이면 로그아웃상태.

    @OneToOne
    @JoinColumn(name = "USER_ID", nullable = false, unique = true)
    private User user;

    public void addRemainTime(Long time) {
        this.remainTime += time;
    }

    public void addEndTime(Long time) {
        this.endTime = this.endTime.plusSeconds(time);
    }

    public RemainTime() {}
    public RemainTime(User user) {
        this.user = user;
        this.remainTime = 0L;
        user.setRemainTime(this);
    }

    public void logout() {
        if(this.endTime.isAfter(LocalDateTime.now())) { // 아직 endTime 이 되지 않았을경우.
            this.remainTime = ChronoUnit.SECONDS.between(LocalDateTime.now(), endTime);
        } else {
            this.remainTime = 0L; // endTime 이 지난경우 강제 로그아웃 되고, remainTime = 0
        }
        this.endTime = null; // 로그아웃 하는경우 endTime = null;
    }

    public void login(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
