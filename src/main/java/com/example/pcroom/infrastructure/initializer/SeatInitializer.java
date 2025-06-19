package com.example.pcroom.infrastructure.initializer;

import com.example.pcroom.domain.Seat;
import com.example.pcroom.domain.SeatStatus;
import com.example.pcroom.infrastructure.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class SeatInitializer implements CommandLineRunner {

    private final SeatRepository seatRepository;

    @Autowired
    public SeatInitializer(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if(seatRepository.count() == 0) {
            for(int i = 1; i <= 30; i++){
                Seat seat = new Seat();
                seat.setSeatNumber(i);
                seat.setSeatStatus(SeatStatus.EMPTY);
                seatRepository.save(seat);
            }
        }
    }
}
