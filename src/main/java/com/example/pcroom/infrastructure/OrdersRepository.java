package com.example.pcroom.infrastructure;

import com.example.pcroom.domain.Orders;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class OrdersRepository {

    @PersistenceContext
    private EntityManager em;

    public Orders save(Orders orders) {
        em.persist(orders);
        return orders;
    }
}
