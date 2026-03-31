package com.example.pcroom.application.scheduler;

import com.example.pcroom.application.LogoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
