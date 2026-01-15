package com.example.pcroom.infrastructure;

import com.example.pcroom.domain.RefreshToken;
import com.example.pcroom.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RefreshTokenRepository {

    @PersistenceContext
    private EntityManager em;

    public RefreshToken save(RefreshToken refreshToken) {
        em.persist(refreshToken);
        return refreshToken;
    }

    public void deleteByUser(User user) {
        em.createQuery("delete from RefreshToken rt where rt.user = :user")
                .setParameter("user", user)
                .executeUpdate();
    }

    public Optional<RefreshToken> findByUserAndHashedToken(User user, String hashedToken) {
        try {
            RefreshToken refreshToken = em.createQuery("select rt from RefreshToken rt where rt.user = :user and rt.tokenHash = :hashedToken", RefreshToken.class)
                    .setParameter("user", user)
                    .setParameter("hashedToken", hashedToken)
                    .getSingleResult();
            return Optional.of(refreshToken);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
