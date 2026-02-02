package com.example.pcroom.application;

import com.example.pcroom.domain.*;
import com.example.pcroom.domain.exception.*;
import com.example.pcroom.infrastructure.*;
import com.example.pcroom.infrastructure.security.JwtUtil;
import com.example.pcroom.presentation.LoginRequestDto;
import com.example.pcroom.presentation.LoginResult;
import com.example.pcroom.presentation.ReissueResponse;
import com.example.pcroom.presentation.user.UserSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final RemainTimeRepository remainTimeRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(UserRepository userRepository, SeatRepository seatRepository, RemainTimeRepository remainTimeRepository, AuthenticationManager authenticationManager, RefreshTokenRepository refreshTokenRepository , JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.seatRepository = seatRepository;
        this.remainTimeRepository = remainTimeRepository;
        this.authenticationManager = authenticationManager;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
    }

    public LoginResult login(LoginRequestDto loginRequestDto) {
        log.info("[Login] request username{}, password{}",
                loginRequestDto.getUsername(),
                loginRequestDto.getPassword());

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getUsername(),
                            loginRequestDto.getPassword()
                    )
            );// id and password 검증. 잘못되면 exception 던져진다.
        } catch (Exception e) {
            log.warn("[Login] authentication failed username={}",
                    loginRequestDto.getUsername());

            throw e;
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // 가져온 인증객체에서 유저정보 가져오기.

        log.debug("[Login] authentication success username={}",
                userDetails.getUsername());

        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        // accesstoken, refreshtoken generate

        User loginUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> {
                    // 인증 후에 유저를 못찾는 상황은 중요 에러
                    log.error("[Login] user not found after authencation username = {}",
                            userDetails.getUsername());

                    return new UsernameNotFoundException("username not found");
                });

        refreshTokenRepository.deleteByUserId(loginUser.getId());
        // 존재하던 refreshToken 삭제 해주거나, 아니면 db에 로그기록으로 남기거나 선택

        RefreshToken newRefreshToken = RefreshToken.create(loginUser.getId(), hash(refreshToken), Duration.ofDays(14));
        refreshTokenRepository.save(newRefreshToken);
        // refreshtoken db 에 저장

        Seat seat = seatRepository.findBySeatNumber(loginRequestDto.getSeatNumber());

        LocalDateTime endTime;
        RemainTime remainTime = remainTimeRepository.findRemainTime(loginUser.getId())
                .orElseThrow(() -> {
                    log.warn("[Login] remain time entity not made userId={}",
                            loginUser.getId());
                    return new RemainTimeNotFoundException("remain time entity not found");
                });

        if(remainTime.getRemainTime() == 0) {
            // 남은시간이 없는경우
            log.warn("[Login] no remain time userId = {}",
                    loginUser.getId());
            throw new NoRemainTimeException("시간을 충전해주세요.");
        } else {
            endTime = LocalDateTime.now().plusSeconds(remainTime.getRemainTime()); // 남은시간이 있는경우 현재시간+남은시간으로 endTime 계산
            remainTime.login(endTime); // 로그인한 경우 remainTime 을 저장.

            log.info("[Login] login time={}, userId={}",
                    LocalDateTime.now(),
                    loginUser.getId());
        }

        SeatHistory seatHistory = new SeatHistory(seat, loginUser, LocalDateTime.now(), null);

        seat.addSeatHistory(seatHistory);
        seat.setSeatStatus(SeatStatus.USING);
        // transaction 이 끝나면 자동으로 영속성컨텍스트의 seat 이 변경감지를 통해 update 되고
        // Cascade.ALL 의 설정으로 seatHistory 역시 자동으로 데이터베이스에 저장이 된다.

        UserSummary userSummary = new UserSummary(loginUser.getId(), loginUser.getName(), loginUser.getRole(), endTime);

        LoginResult loginResult = new LoginResult(accessToken, refreshToken, userSummary);
        // endTime 정보를 클라이언트에게 보내 화면에 띄어줌.

        log.info("[Login] success userId={}",
                loginUser.getId());

        return loginResult;
    }

    public ReissueResponse reissue(String refreshToken) {
        log.info("[Reissue] request start");

        if (refreshToken == null || refreshToken.isEmpty()) {
            log.warn("[Reissue] refresh token is empty");
            throw new RefreshTokenNotFoundException("refresh token not found");
        }
        if (jwtUtil.isTokenExpired(refreshToken)) {
            log.warn("[Reissue] refresh token is expired");
            throw new RefreshTokenExpiredException("refresh token expired");
        }
        String username = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    // 이 경우 토큰이 있는데 user 가 없음.
                    // 토큰 위조, 탈취 가능성
                    log.error("[Reissue] user not found with refresh token");
                    return new UsernameNotFoundException("username not found");
                });

        RefreshToken savedRefreshToken = refreshTokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    // 이 경우 저장이 잘못되거나 토큰의 위조 가능성
                    log.warn("[Reissue] refresh token not found userId={}",
                            user.getId());
                    return new RefreshTokenNotFoundException("refresh token not found");
                });

        if(savedRefreshToken.isRevoked()){
            log.warn("[Reissue] revoked refresh token userId={}",
                    user.getId());
            throw new RefreshTokenRevokedException("refresh token revoked");
        }
        if(savedRefreshToken.isExpired()){
            log.warn("[Reissue] refresh token expired userId={}",
                    user.getId());
            throw new RefreshTokenExpiredException("refresh token expired");
        }

        String newAccessToken = jwtUtil.generateAccessToken(user);
        UserSummary userSummary = new UserSummary(
                user.getId(),
                username,
                user.getRole(),
                user.getRemainTime().getEndTime()
        );

        log.info("[Reissue] success userId={}",
                user.getId());

        return new ReissueResponse(newAccessToken, userSummary);
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not found", e);
        }
    }
}
