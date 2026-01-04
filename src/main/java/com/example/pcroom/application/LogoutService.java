package com.example.pcroom.application;

import com.example.pcroom.domain.RemainTime;
import com.example.pcroom.domain.SeatHistory;
import com.example.pcroom.domain.SeatStatus;
import com.example.pcroom.domain.User;
import com.example.pcroom.domain.exception.NoRemainTimeException;
import com.example.pcroom.domain.exception.NoUserActiveSeatException;
import com.example.pcroom.infrastructure.RemainTimeRepository;
import com.example.pcroom.infrastructure.SeatHistoryRepository;
import com.example.pcroom.infrastructure.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogoutService {

    private final RemainTimeRepository remainTimeRepository;
    private final UserRepository userRepository;
    private final SeatHistoryRepository seatHistoryRepository;

    @Autowired
    public LogoutService(RemainTimeRepository remainTimeRepository, UserRepository userRepository, SeatHistoryRepository seatHistoryRepository) {
        this.remainTimeRepository = remainTimeRepository;
        this.userRepository = userRepository;
        this.seatHistoryRepository = seatHistoryRepository;
    }

    public void logoutUser(Long userId) { // 시간이 끝나기 전 직접 로그아웃 시도시
        User user = userRepository.findById(userId);
        RemainTime remainTime = remainTimeRepository.findRemainTime(userId)
                .orElseThrow(() -> new NoRemainTimeException("이 유저는 사용중이지 않습니다."));

        logout(user, remainTime);
    }

    public void logoutEndTime() { // 종료시간이 지난 좌석들 로그아웃
        List<RemainTime> remainTimes = remainTimeRepository.findRemainTimeAfterNow(LocalDateTime.now()); // 종료시간이 지난 레코드들을 찾는다

        for(RemainTime remainTime : remainTimes) {
            User user = remainTime.getUser();
            logout(user, remainTime); // 실제 로그아웃 메서드 실행
        }
    }

    public void logout(User user, RemainTime remainTime) {
        remainTime.logout();

        SeatHistory seatHistory = seatHistoryRepository.findActiveByUser(user) // 로그기록중 현재 로그인중인 기록을 찾는다.
                .orElseThrow(() -> new NoUserActiveSeatException("이 유저는 사용중이지 않습니다."));

        seatHistory.setEndTime(LocalDateTime.now()); // 현재 사용중인 로그기록의 종료시간을 null 에서 현재시간으로 update
        seatHistory.getSeat().setSeatStatus(SeatStatus.EMPTY); // 좌석의 상태를 빈자리로 설정
    }
}
