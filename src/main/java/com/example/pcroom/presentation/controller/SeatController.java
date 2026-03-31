package com.example.pcroom.presentation.controller;

import com.example.pcroom.application.SeatService;
import com.example.pcroom.presentation.seat.SeatResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/seat")
public class SeatController {

    private final SeatService seatService;

    @Autowired
    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping
    public ResponseEntity<List<SeatResponseDto>> getSeats() {
        List<SeatResponseDto> seatResponseDtos = seatService.getSeats();

        return ResponseEntity.ok(seatResponseDtos);
    }
}
