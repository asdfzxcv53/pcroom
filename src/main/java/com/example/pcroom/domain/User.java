package com.example.pcroom.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "user")
    private List<SeatHistory> seatHistory;

    @OneToMany(mappedBy = "user")
    private List<Orders> orders;
}
