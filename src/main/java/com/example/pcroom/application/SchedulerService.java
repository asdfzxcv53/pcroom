package com.example.pcroom.application;

import com.example.pcroom.domain.*;
import com.example.pcroom.domain.exception.NoUserActiveSeatException;
import com.example.pcroom.infrastructure.RemainTimeRepository;
import com.example.pcroom.infrastructure.SeatHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SchedulerService {

    private final RemainTimeRepository remainTimeRepository;
    private final SeatHistoryRepository seatHistoryRepository;

    @Autowired
    public SchedulerService(RemainTimeRepository remainTimeRepository, SeatHistoryRepository seatHistoryRepository) {
        this.remainTimeRepository = remainTimeRepository;
        this.seatHistoryRepository = seatHistoryRepository;
    }

    @Scheduled(fixedRate = 6000)
    public void logout() {
        List<RemainTime> remainTimes = remainTimeRepository.findRemainTimeAfterNow(LocalDateTime.now());
        for (RemainTime remainTime : remainTimes) {
            remainTime.logout(); // 사용한 시간을 계산하여 남은시간을 저장시킨다.
            User user = remainTime.getUser();

            SeatHistory seatHistory = seatHistoryRepository.findActiveByUser(user)
                    .orElseThrow(() -> new NoUserActiveSeatException("이 유저는 사용중이지 않습니다."));

            seatHistory.setEndTime(LocalDateTime.now()); // 로그기록에 종료시간을 기록한다.
            seatHistory.getSeat().setSeatStatus(SeatStatus.EMPTY); // 좌석의 상태를 바꾼다 ( 사용중 -> 빈자리 )
        }
    }
}
