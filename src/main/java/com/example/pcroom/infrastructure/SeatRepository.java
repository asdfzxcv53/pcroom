package com.example.pcroom.infrastructure;

import com.example.pcroom.domain.Seat;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

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

    public Optional<Seat> findBySeatNumber(int seatNumber) {
        List<Seat> seats = em.createQuery("select s from Seat s where s.seatNumber = :seatNumber", Seat.class)
                .setParameter("seatNumber", seatNumber)
                .getResultList();

        return seats.stream().findFirst();
    }



    public Long count() {
        return em.createQuery("select count(s) from Seat s", Long.class)
                .getSingleResult();
    }
}
