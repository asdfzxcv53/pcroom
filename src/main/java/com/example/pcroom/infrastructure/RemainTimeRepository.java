package com.example.pcroom.infrastructure;

import com.example.pcroom.domain.RemainTime;
import com.example.pcroom.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class RemainTimeRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(RemainTime remainTime) {
        em.persist(remainTime);
    }

    public Optional<RemainTime> findRemainTime(Long userId) {
        try {
            RemainTime remainTime = em.createQuery("select r from RemainTime r where r.user.id = :userId", RemainTime.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
            return Optional.of(remainTime);
        } catch(NoResultException e) {
            return Optional.empty();
        }
    }
    public List<RemainTime> findRemainTimeAfterNow(LocalDateTime now){
        return em.createQuery("select r from RemainTime r where r.endTime < :now", RemainTime.class)
                .setParameter("now", now)
                .getResultList();
    }
}
