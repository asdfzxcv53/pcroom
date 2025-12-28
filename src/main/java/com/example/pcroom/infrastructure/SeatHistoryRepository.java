package com.example.pcroom.infrastructure;

import com.example.pcroom.domain.Seat;
import com.example.pcroom.domain.SeatHistory;
import com.example.pcroom.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SeatHistoryRepository {

    @PersistenceContext
    private EntityManager em;

    public SeatHistory save(SeatHistory seatHistory) {
        em.persist(seatHistory);

        return seatHistory;
    }

    public List<SeatHistory> findByUserId(Long userId) {
        return em.createQuery("select sh from SeatHistory sh where sh.user.id = :userId", SeatHistory.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<SeatHistory> findBySeatId(Long seatId) {
        return em.createQuery("select sh from SeatHistory sh where sh.seat.id = :seatId", SeatHistory.class)
                .setParameter("seatId", seatId)
                .getResultList();
    }

    public Optional<SeatHistory> findActiveByUser(User user){ // 사용중인 기록을 찾는다.
        return em.createQuery("select sh from SeatHistory sh where sh.user = :user and sh.endTime is null", SeatHistory.class)
                .setParameter("user", user)
                .getResultList().stream().findFirst();
    }
}
