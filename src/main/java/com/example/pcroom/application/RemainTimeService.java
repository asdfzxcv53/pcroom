package com.example.pcroom.application;

import com.example.pcroom.domain.RemainTime;
import com.example.pcroom.infrastructure.RemainTimeRepository;
import com.example.pcroom.presentation.remaintime.RemainTimeRequestDto;
import com.example.pcroom.presentation.remaintime.RemainTimeResponseDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class RemainTimeService {

    private final RemainTimeRepository remainTimeRepository;

    public RemainTimeService(RemainTimeRepository remainTimeRepository) {
        this.remainTimeRepository = remainTimeRepository;
    }

    public RemainTimeResponseDto addRemainTime(RemainTimeRequestDto remainTimeRequestDto) {
        // 로그인 되어있으면 endTime 을 더해주고 로그아웃 되어있으면 remainTime 에 더해준다.

        RemainTime remainTime = remainTimeRepository.findRemainTime(remainTimeRequestDto.getMemberId()).orElseThrow();

        if(remainTime.getEndTime() == null) {
            // null 이면 로그아웃 상태니까 remainTime 에 충전하는 시간을 더해준다.

            remainTime.addRemainTime(remainTimeRequestDto.getAddTime());

        } else {
            // null이 아니면 로그인 상태니까 endTime 에 충전하는 시간을 더해준다.

            remainTime.addEndTime(remainTimeRequestDto.getAddTime());
        }
        RemainTimeResponseDto remainTimeResponseDto = new RemainTimeResponseDto(remainTimeRequestDto.getAddTime(), remainTime.getEndTime());

        return remainTimeResponseDto;
    }
}
