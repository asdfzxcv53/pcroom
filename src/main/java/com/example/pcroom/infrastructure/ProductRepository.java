package com.example.pcroom.infrastructure;

import com.example.pcroom.domain.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepository {

    @PersistenceContext
    private EntityManager em;

    public Product save(Product product) {
        em.persist(product);
        return product;
    }

    public List<Product> findAll() {
        return em.createQuery("select p from Product p ", Product.class).getResultList();
    }
}
