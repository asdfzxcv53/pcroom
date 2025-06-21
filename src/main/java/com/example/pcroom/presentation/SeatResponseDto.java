package com.example.pcroom.presentation;

import com.example.pcroom.domain.Seat;
import com.example.pcroom.domain.SeatStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatResponseDto {
    private Long seatId;
    private int seatNumber;
    private SeatStatus seatStatus;

    public static SeatResponseDto fromEntity(Seat seat) {
        SeatResponseDto seatResponseDto = new SeatResponseDto();

        seatResponseDto.seatId = seat.getId();
        seatResponseDto.seatNumber = seat.getSeatNumber();
        seatResponseDto.seatStatus = seat.getSeatStatus();

        return seatResponseDto;
    }
}