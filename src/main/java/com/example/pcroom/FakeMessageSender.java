package com.example.pcroom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FakeMessageSender {

    public void sendMessage(String payload) {
        log.info("주문이 완료되었습니다. 주문목록 : {}", payload);
    }
}
