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

    private final LogoutService logoutService;

    @Autowired
    public SchedulerService(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    @Scheduled(fixedRate = 6000)
    public void logout() {
        logoutService.logoutEndTime();
    }
}
