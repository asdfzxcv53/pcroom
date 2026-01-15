package com.example.pcroom.application;

import com.example.pcroom.domain.*;
import com.example.pcroom.domain.exception.*;
import com.example.pcroom.infrastructure.*;
import com.example.pcroom.infrastructure.security.JwtUtil;
import com.example.pcroom.presentation.LoginRequestDto;
import com.example.pcroom.presentation.LoginResult;
import com.example.pcroom.presentation.ReissueResponse;
import com.example.pcroom.presentation.user.UserSummary;
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

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(),
                        loginRequestDto.getPassword()
                )
        ); // id and password 검증. 잘못되면 exception 던져진다.

        UserDetails userDetails = (UserDetails) authentication.getDetails();
        // 가져온 인증객체에서 유저정보 가져오기.

        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        // accesstoken, refreshtoken generate

        User loginUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));

        refreshTokenRepository.deleteByUser(loginUser);
        // 존재하던 refreshToken 삭제 해주거나, 아니면 db에 로그기록으로 남기거나 선택

        RefreshToken dbRefreshToken = RefreshToken.create(loginUser, hash(refreshToken), Duration.ofDays(14));
        refreshTokenRepository.save(dbRefreshToken);
        // refreshtoken db 에 저장

        Seat seat = seatRepository.findBySeatNumber(loginRequestDto.getSeatNumber());

        LocalDateTime endTime;
        Optional<RemainTime> remainTime = remainTimeRepository.findRemainTime(loginUser.getId());
        if(remainTime.get().getRemainTime() == 0){
            throw new NoRemainTimeException("시간을 충전해주세요."); // 남은시간이 없는경우 충전하라고 exception 보냄
        } else {
            endTime = LocalDateTime.now().plusSeconds(remainTime.get().getRemainTime()); // 남은시간이 있는경우 현재시간+남은시간으로 endTime 계산
            remainTime.get().login(endTime); // 로그인한 경우 remainTime 을 저장.
        }

        SeatHistory seatHistory = new SeatHistory(seat, loginUser, LocalDateTime.now(), null);

        seat.addSeatHistory(seatHistory);
        seat.setSeatStatus(SeatStatus.USING);
        // transaction 이 끝나면 자동으로 영속성컨텍스트의 seat 이 변경감지를 통해 update 되고
        // Cascade.ALL 의 설정으로 seatHistory 역시 자동으로 데이터베이스에 저장이 된다.

        UserSummary userSummary = new UserSummary(loginUser.getId(), loginUser.getName(), loginUser.getRole(), endTime);

        LoginResult loginResult = new LoginResult(accessToken, refreshToken, userSummary);
        // endTime 정보를 클라이언트에게 보내 화면에 띄어줌.

        return loginResult;
    }

    public ReissueResponse reissue(String refreshToken) {
        if(refreshToken == null || refreshToken.isEmpty()){
            throw new RefreshTokenNotFoundException("refresh token not found");
        }
        if(jwtUtil.isTokenExpired(refreshToken)){
            throw new RefreshTokenExpiredException("refresh token expired");
        }
        String username = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));

        String hashedToken = hash(refreshToken);

        RefreshToken savedRefreshToken = refreshTokenRepository.findByUserAndHashedToken(user, hashedToken)
                .orElseThrow(() -> new RefreshTokenNotFoundException("refresh token not found"));

        if(savedRefreshToken.isRevoked()){
            throw new RefreshTokenRevokedException("refresh token revoked");
        }
        if(savedRefreshToken.isExpired()){
            throw new RefreshTokenExpiredException("refresh token expired");
        }

        String newAccessToken = jwtUtil.generateAccessToken(user);
        UserSummary userSummary = new UserSummary(user.getId(), username, user.getRole(), user.getRemainTime().getEndTime());

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
