package com.example.pcroom.infrastructure;

import com.example.pcroom.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager em;

    public User save(User user) {
        em.persist(user);

        return user;
    }

    public Optional<User> findById(Long id) {

        User user = em.find(User.class, id);

        return Optional.ofNullable(user);
    }

    public Optional<User> findByUsername(String username) {
        try {
            User user = em.createQuery("select u from User u where u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.of(user);
        } catch(NoResultException e) {
            return Optional.empty();
        }
    }
}
