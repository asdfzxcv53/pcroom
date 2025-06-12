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

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


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

    @Test
    @DisplayName("전체 상품 저장")
    public void findAllProductsTest() throws Exception {

        // Given

        Product product1 = Product.builder()
                .name("milk")
                .price(3000)
                .quantity(30)
                .build();

        Product product2 = Product.builder()
                .name("choco")
                .price(1000)
                .quantity(10)
                .build();

        productRepository.save(product1);
        productRepository.save(product2);

        // When

        List<Product> result = productRepository.findAll();

        // Then

        assertThat(result)
                .hasSize(2);

        assertThat(result)
                .extracting("name", "price", "quantity")
                .containsExactly(
                        tuple("milk", 3000, 30),
                        tuple("choco", 1000, 10)
                );

    }

    @Test
    @DisplayName("기본키로 상품 검색")
    public void findProductByIdTest() throws Exception {

        // Given

        Product product1 = Product.builder()
                .name("milk")
                .price(3000)
                .quantity(30)
                .build();

        Product product2 = Product.builder()
                .name("choco")
                .price(1000)
                .quantity(10)
                .build();

        productRepository.save(product1);
        productRepository.save(product2);

        // When

        Product id1 = productRepository.findById(4L);
        Product id2 = productRepository.findById(5L);

        // Then

        assertThat(id1)
                .extracting("name", "price", "quantity")
                .containsExactly("milk", 3000, 30);

        assertThat(id2)
                .extracting("name", "price", "quantity")
                .containsExactly("choco", 1000, 10);

    }
}
