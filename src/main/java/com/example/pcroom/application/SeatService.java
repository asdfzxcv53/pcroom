package com.example.pcroom.application;

import com.example.pcroom.domain.Seat;
import com.example.pcroom.infrastructure.SeatRepository;
import com.example.pcroom.presentation.SeatResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    @Autowired
    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public List<SeatResponseDto> getSeats() {
        List<Seat> seats = seatRepository.findAll();

        List<SeatResponseDto> seatResponseDtos = seats
                .stream()
                .map(seat -> SeatResponseDto.fromEntity(seat))
                .toList();

        return seatResponseDtos;
    }
}
