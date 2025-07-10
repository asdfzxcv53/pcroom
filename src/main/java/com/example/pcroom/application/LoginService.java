package com.example.pcroom.application;

import com.example.pcroom.domain.*;
import com.example.pcroom.domain.exception.NoRemainTimeException;
import com.example.pcroom.domain.exception.PasswordNotMatchException;
import com.example.pcroom.domain.exception.UsernameNotMatchException;
import com.example.pcroom.infrastructure.RemainTimeRepository;
import com.example.pcroom.infrastructure.SeatHistoryRepository;
import com.example.pcroom.infrastructure.SeatRepository;
import com.example.pcroom.infrastructure.UserRepository;
import com.example.pcroom.presentation.LoginRequestDto;
import com.example.pcroom.presentation.LoginResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class LoginService {

    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final RemainTimeRepository remainTimeRepository;

    @Autowired
    public LoginService(UserRepository userRepository, SeatRepository seatRepository, RemainTimeRepository remainTimeRepository) {
        this.userRepository = userRepository;
        this.seatRepository = seatRepository;
        this.remainTimeRepository = remainTimeRepository;
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

        Optional<User> user = checkValidation(loginRequestDto);
        User loginUser = user.get();

        Seat seat = seatRepository.findBySeatNumber(loginRequestDto.getSeatNumber());

        LocalDateTime endTime;
        Optional<RemainTime> remainTime = remainTimeRepository.findRemainTime(loginUser.getId());
        if(remainTime.get().getRemainTime() == 0){
            throw new NoRemainTimeException("시간을 충전해주세요."); // 남은시간이 없는경우 충전하라고 exception 보냄
        } else {
            endTime = LocalDateTime.now().plusSeconds(remainTime.get().getRemainTime()); // 남은시간이 있는경우 현재시간+남은시간으로 endTime 계산
            remainTime.get().login(endTime); // 로그인한 경우 remainTime 을 저장.
        }

        SeatHistory seatHistory = new SeatHistory(seat, loginUser, LocalDateTime.now(), endTime , CurrentStatus.USE);

        seat.addSeatHistory(seatHistory);
        // transaction 이 끝나면 자동으로 영속성컨텍스트의 seat 이 변경감지를 통해 update 되고
        // Cascade.ALL 의 설정으로 seatHistory 역시 자동으로 데이터베이스에 저장이 된다.

        LoginResponseDto loginResponseDto = new LoginResponseDto(loginUser.getName(), endTime);
        // endTime 정보를 클라이언트에게 보내 화면에 띄어줌.

        return loginResponseDto;
    }

    // 로그인 시 아이디와 페스워드 일치 체크
    private Optional<User> checkValidation(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        Optional<User> user = userRepository.findByUsername(username);

        if(user.isPresent()) {
            if(!user.get().getPassword().equals(password)) {
                throw(new PasswordNotMatchException("password not match"));
            }
        } else {
            throw(new UsernameNotMatchException("username not match"));
        }

        return user;
    }
}
