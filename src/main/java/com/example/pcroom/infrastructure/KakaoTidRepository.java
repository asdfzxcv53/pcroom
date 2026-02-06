package com.example.pcroom.infrastructure;

import com.example.pcroom.domain.KakaoTid;
import com.example.pcroom.domain.Orders;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class KakaoTidRepository {

    @PersistenceContext
    private EntityManager em;

    public KakaoTid save(KakaoTid kakaoTid) {
        em.persist(kakaoTid);

        return kakaoTid;
    }

    public Optional<KakaoTid> findByKakaoTid(String tid) {
        KakaoTid kakaoTid =  em.createQuery("select kt from KakaoTid kt where kt.tid = :tid", KakaoTid.class)
                .setParameter("tid", tid)
                .getSingleResult();

        return Optional.ofNullable(kakaoTid);
    }

    public Optional<KakaoTid> findByOrders(Orders orders) {
        KakaoTid kakaoTid = em.createQuery("select k from KakaoTid k where k.orders = :orders", KakaoTid.class)
                .setParameter("orders", orders)
                .getSingleResult();

        return Optional.ofNullable(kakaoTid);
    }
}
