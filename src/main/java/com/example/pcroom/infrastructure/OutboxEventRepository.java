package com.example.pcroom.infrastructure;

import com.example.pcroom.domain.OutboxEvent;
import com.example.pcroom.domain.OutboxStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OutboxEventRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(OutboxEvent outboxEvent) {
        em.persist(outboxEvent);
    }

    public List<OutboxEvent> findByStatus(OutboxStatus status) {
        return em.createQuery("select oe from OutboxEvent oe where oe.status = :status", OutboxEvent.class)
                .setParameter("status", status)
                .getResultList();
    }
}
