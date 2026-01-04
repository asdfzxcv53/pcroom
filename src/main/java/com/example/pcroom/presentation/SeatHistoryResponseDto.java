package com.example.pcroom.presentation;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SeatHistoryResponseDto {

    private int seatNumber;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
