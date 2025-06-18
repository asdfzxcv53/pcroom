package com.example.pcroom.infrastructure;

import com.example.pcroom.domain.Orders;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrdersRepository {

    @PersistenceContext
    private EntityManager em;

    public Orders save(Orders orders) {
        em.persist(orders);
        return orders;
    }

    public List<Orders> findOrdersByUserId(Long userId) {
        return em.createQuery("select o from Orders o " +
                        "join fetch o.ordersProducts op " +
                        "where o.user.id = :userId", Orders.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
