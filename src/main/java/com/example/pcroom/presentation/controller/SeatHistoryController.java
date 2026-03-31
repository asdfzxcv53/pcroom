package com.example.pcroom.presentation.controller;

import com.example.pcroom.application.SeatHistoryService;
import com.example.pcroom.presentation.seat.SeatHistoryResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/seathistory")
public class SeatHistoryController {

    private final SeatHistoryService seatHistoryService;

    @Autowired
    public SeatHistoryController(SeatHistoryService seatHistoryService) {
        this.seatHistoryService = seatHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<SeatHistoryResponseDto>> getSeatHistory() {
        List<SeatHistoryResponseDto> seatHistoryResponseDtos = seatHistoryService.getSeatHistory();

        return ResponseEntity.ok(seatHistoryResponseDtos);
    }
}
