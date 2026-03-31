package com.example.pcroom.infrastructure;

import com.example.pcroom.domain.OrderStatus;
import com.example.pcroom.domain.Orders;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrdersRepository {

    @PersistenceContext
    private EntityManager em;

    public Orders save(Orders orders) {
        em.persist(orders);
        return orders;
    }

    public Optional<Orders> findById(Long id) {
        return Optional.ofNullable(em.find(Orders.class, id));
    }

    public List<Orders> findOrdersByUserId(Long userId) {
        return em.createQuery("select o from Orders o " +
                        "join fetch o.ordersProducts op " +
                        "where o.user.id = :userId", Orders.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public int updateStatusIfPending(Long orderId, OrderStatus status) {
        return em.createQuery(
                "update Orders o " +
                "set o.status = :status " +
                "where o.id = :orderId " +
                "and o.status = :pending"
        )
                .setParameter("status", status)
                .setParameter("orderId", orderId)
                .setParameter("pending", OrderStatus.PENDING)
                .executeUpdate();
    }

    public List<Orders> findApprovingOrdersWithTid(OrderStatus status) {
        return em.createQuery(
                    "select o from Orders o " +
                        "join fetch o.kakaoTid kt " +
                        "where o.status = :status",
                        Orders.class
        )
                .setParameter("status", status)
                .getResultList();
    }
}
