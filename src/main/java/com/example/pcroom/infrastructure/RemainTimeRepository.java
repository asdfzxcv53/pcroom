package com.example.pcroom.infrastructure;

import com.example.pcroom.domain.RemainTime;
import com.example.pcroom.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class RemainTimeRepository {

    @PersistenceContext
    private EntityManager em;
}
