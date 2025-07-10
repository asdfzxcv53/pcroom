package com.example.pcroom.presentation.remaintime;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RemainTimeResponseDto {

    private Long addTime;
    private LocalDateTime endTime;

    public RemainTimeResponseDto() {}
    public RemainTimeResponseDto(Long addTime, LocalDateTime endTime) {
        this.addTime = addTime;
        this.endTime = endTime;
    }
}
