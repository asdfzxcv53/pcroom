package com.example.pcroom.application;

import com.example.pcroom.domain.RemainTime;
import com.example.pcroom.infrastructure.RemainTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SchedulerService {

    private final RemainTimeRepository remainTimeRepository;

    @Autowired
    public SchedulerService(RemainTimeRepository remainTimeRepository) {
        this.remainTimeRepository = remainTimeRepository;
    }

    @Scheduled(fixedRate = 6000)
    public void logout() {
        List<RemainTime> remainTimes = remainTimeRepository.findRemainTimeAfterNow(LocalDateTime.now());
        for (RemainTime remainTime : remainTimes) {
            remainTime.logout();
        }
    }
}
