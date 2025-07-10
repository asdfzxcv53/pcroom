package com.example.pcroom.presentation.controller;

import com.example.pcroom.application.RemainTimeService;
import com.example.pcroom.presentation.remaintime.RemainTimeRequestDto;
import com.example.pcroom.presentation.remaintime.RemainTimeResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/remainTime")
public class RemainTimeController {

    private final RemainTimeService remainTimeService;

    public RemainTimeController(RemainTimeService remainTimeService) {
        this.remainTimeService = remainTimeService;
    }

    @PatchMapping
    public ResponseEntity<RemainTimeResponseDto> addRemainTime(@RequestBody RemainTimeRequestDto remainTimeRequestDto) {
        RemainTimeResponseDto remainTimeResponseDto = remainTimeService.addRemainTime(remainTimeRequestDto);

        return ResponseEntity.ok(remainTimeResponseDto);
    }
}
