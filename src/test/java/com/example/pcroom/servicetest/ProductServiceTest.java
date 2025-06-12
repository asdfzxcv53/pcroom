package com.example.pcroom.servicetest;

import com.example.pcroom.application.ProductService;
import com.example.pcroom.domain.Product;
import com.example.pcroom.infrastructure.ProductRepository;
import com.example.pcroom.presentation.ProductResponseDto;
import com.example.pcroom.presentation.ProductSaveRequestDto;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;


import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품 저장")
    public void saveProductTest() throws Exception {

        // Given

        Product product = Product.builder()
                .name("milk")
                .price(3000)
                .quantity(30)
                .build();
        product.setId(1L);

        ProductSaveRequestDto productSaveRequestDto = new ProductSaveRequestDto();
        productSaveRequestDto.setName("milk");
        productSaveRequestDto.setPrice(3000);
        productSaveRequestDto.setQuantity(30);

        when(productRepository.save(any(Product.class))).thenReturn(product);

        // When

        ProductResponseDto result = productService.save(productSaveRequestDto);

        // Then

        assertThat(result)
                .extracting("name", "price", "quantity")
                .containsExactly("milk", 3000, 30);
    }

    @Test
    @DisplayName("전체 상품 조회")
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

        List<Product> products = List.of(product1, product2);

        when(productRepository.findAll()).thenReturn(products);

        // When

        List<ProductResponseDto> result = productService.findAll();

        // Then

        assertThat(result)
                .hasSize(2);

        assertThat(result)
                .extracting("name")
                .containsExactly("milk", "choco");

        assertThat(result)
                .extracting("name", "price", "quantity")
                .containsExactly(
                        tuple("milk", 3000, 30),
                        tuple("choco", 1000, 10)
                );

    }
}
