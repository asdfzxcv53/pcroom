package com.example.pcroom.application;

import com.example.pcroom.domain.SeatHistory;
import com.example.pcroom.infrastructure.SeatHistoryRepository;
import com.example.pcroom.presentation.seat.SeatHistoryResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeatHistoryService {

    private final SeatHistoryRepository seatHistoryRepository;

    @Autowired
    public SeatHistoryService(SeatHistoryRepository seatHistoryRepository) {
        this.seatHistoryRepository = seatHistoryRepository;
    }

    public List<SeatHistoryResponseDto> getSeatHistory() {

        List<SeatHistory> seatHistories = seatHistoryRepository.findAll();

        List<SeatHistoryResponseDto> seatHistoryResponseDtos = new ArrayList<>();
        for (SeatHistory seatHistory : seatHistories) {
            SeatHistoryResponseDto seatHistoryResponseDto = new SeatHistoryResponseDto();

            seatHistoryResponseDto.setSeatNumber(seatHistory.getSeat().getSeatNumber());
            seatHistoryResponseDto.setName(seatHistory.getUser().getName());
            seatHistoryResponseDto.setStartTime(seatHistory.getStartTime());
            seatHistoryResponseDto.setEndTime(seatHistory.getEndTime());

            seatHistoryResponseDtos.add(seatHistoryResponseDto);
        }
        return seatHistoryResponseDtos;
    }
}
