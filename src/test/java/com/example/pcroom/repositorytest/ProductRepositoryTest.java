package com.example.pcroom.repositorytest;

import com.example.pcroom.domain.Product;
import com.example.pcroom.infrastructure.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품 저장")
    public void saveProductTest() throws Exception {

        Product product = Product.builder()
                .name("milk")
                .price(3000)
                .quantity(30)
                .build();

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct)
                .isNotNull()
                .extracting("name", "price", "quantity")
                .containsExactly("milk", 3000, 30);
    }
}
