package com.example.pcroom.presentation.user;

import com.example.pcroom.domain.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserSummary {
    private Long id;
    private String name;
    private Role role;
    private LocalDateTime endTime;

    public UserSummary() {}
    public UserSummary(Long id, String name, Role role, LocalDateTime endTime) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.endTime = endTime;
    }
}
