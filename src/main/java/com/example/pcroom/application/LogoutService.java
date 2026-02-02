package com.example.pcroom.application;

import com.example.pcroom.domain.*;
import com.example.pcroom.domain.exception.NoRemainTimeException;
import com.example.pcroom.domain.exception.NoUserActiveSeatException;
import com.example.pcroom.domain.exception.RefreshTokenNotFoundException;
import com.example.pcroom.domain.exception.UserNotFoundException;
import com.example.pcroom.infrastructure.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
public class LogoutService {

    private final RemainTimeRepository remainTimeRepository;
    private final UserRepository userRepository;
    private final SeatHistoryRepository seatHistoryRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public LogoutService(RemainTimeRepository remainTimeRepository, UserRepository userRepository, SeatHistoryRepository seatHistoryRepository, RefreshTokenRepository refreshTokenRepository) {
        this.remainTimeRepository = remainTimeRepository;
        this.userRepository = userRepository;
        this.seatHistoryRepository = seatHistoryRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void logoutUser(Long userId, String refreshToken) { // 시간이 끝나기 전 직접 로그아웃 시도시
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("[Logout] user not found userId={}",
                            userId);
                    return new UserNotFoundException("유저를 찾을 수 없습니다.");
                });
        RemainTime remainTime = remainTimeRepository.findRemainTime(userId)
                .orElseThrow(() -> {
                    log.warn("[Logout] remain time not found userId={}",
                            userId);
                    return new NoRemainTimeException("이 유저는 사용중이지 않습니다.");
                });

        logout(user, remainTime);
        revokeToken(user, refreshToken);
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
                .orElseThrow(() -> {
                    log.warn("[Logout] user is not login status userId = {}",
                            user.getId());
                    return new NoUserActiveSeatException("user is not login");
                });

        seatHistory.setEndTime(LocalDateTime.now()); // 현재 사용중인 로그기록의 종료시간을 null 에서 현재시간으로 update
        seatHistory.getSeat().setSeatStatus(SeatStatus.EMPTY); // 좌석의 상태를 빈자리로 설정

        log.info("[Logout] logout success userId = {}",
                user.getId());
    }

    public void revokeToken(User user, String refreshToken) {
        RefreshToken savedToken = refreshTokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    log.warn("[Logout] refresh token not found userId = {}",
                            user.getId());
                    return new RefreshTokenNotFoundException("refresh token not found");
                });

        savedToken.revoke();
    }
}
