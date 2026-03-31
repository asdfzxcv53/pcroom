package com.example.pcroom.application;

import com.example.pcroom.FakeMessageSender;
import com.example.pcroom.domain.OutboxEvent;
import com.example.pcroom.domain.OutboxStatus;
import com.example.pcroom.infrastructure.OutboxEventRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final FakeMessageSender fakeMessageSender;

    public OutboxService(OutboxEventRepository outboxEventRepository, FakeMessageSender fakeMessageSender) {
        this.outboxEventRepository = outboxEventRepository;
        this.fakeMessageSender = fakeMessageSender;
    }

    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> events =
                outboxEventRepository.findByStatus(OutboxStatus.PENDING);

        for(OutboxEvent event : events) {
            fakeMessageSender.sendMessage(event.getPayload());
            event.markSent();
        }
    }
}
