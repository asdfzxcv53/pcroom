package com.example.pcroom.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "user")
    private List<SeatHistory> seatHistory = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Orders> orders = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private RemainTime remainTime; // user 가 회원가입 할때 remainTime 이 0 인 객체를 만들어서 이어주고 persist 를 통해 동시에 영속성 컨텍스트에 저장.

    public User() {}

    @Builder
    public User(String username, String password, String name, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public void addOrders(Orders orders) {
        this.orders.add(orders);
    }

    public void addRemainTime(Long time){
        remainTime.addRemainTime(time);
    }
}
