package com.example.pcroom.infrastructure;

import com.example.pcroom.domain.RefreshToken;
import com.example.pcroom.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("db")
public class DBRefreshTokenRepository implements RefreshTokenRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        em.persist(refreshToken);
        return refreshToken;
    }

    @Override
    public void deleteByUserId(Long userId) {
        em.createQuery("delete from RefreshToken rt where rt.userId = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
    }

    @Override
    public Optional<RefreshToken> findByUserId(Long userId) {
        try {
            RefreshToken refreshToken = em.createQuery("select rt from RefreshToken rt where rt.userId = :userId", RefreshToken.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
            return Optional.of(refreshToken);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
