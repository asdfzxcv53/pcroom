package com.example.pcroom.infrastructure;

import com.example.pcroom.domain.RemainTime;
import com.example.pcroom.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class RemainTimeRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(RemainTime remainTime) {
        em.persist(remainTime);
    }

    public Long findRemainTime(Long userId) {
        try {
            Long remainTime = em.createQuery("select r.remainTime from RemainTime r where r.user.id = :userId", Long.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
            return remainTime;
        } catch(NoResultException e) {
            return null;
        }
    }
}
