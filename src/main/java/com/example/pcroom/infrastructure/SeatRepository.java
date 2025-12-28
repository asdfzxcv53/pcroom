package com.example.pcroom.infrastructure;

import com.example.pcroom.domain.Seat;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SeatRepository {

    @PersistenceContext
    private EntityManager em;

    public Seat save(Seat seat) {
        em.persist(seat);

        return seat;
    }

    public List<Seat> findAll() {
        return em.createQuery("select s from Seat s", Seat.class)
                .getResultList();
    }

    public Seat findBySeatNumber(int seatNumber) {
        return em.createQuery("select s from Seat s where s.seatNumber = :seatNumber", Seat.class)
                .setParameter("seatNumber", seatNumber)
                .getSingleResult();
    }



    public Long count() {
        return em.createQuery("select count(s) from Seat s", Long.class)
                .getSingleResult();
    }
}
