package com.example.pcroom.application;

import com.example.pcroom.domain.Product;
import com.example.pcroom.infrastructure.ProductRepository;
import com.example.pcroom.presentation.product.ProductResponseDto;
import com.example.pcroom.presentation.product.ProductSaveRequestDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponseDto save(ProductSaveRequestDto productSaveRequestDto) {
        log.info("[Product] save product name = {}",
                productSaveRequestDto.getName());

        Product product = productSaveRequestDto.toEntity();

        Product savedProduct = productRepository.save(product);

        ProductResponseDto productResponseDto = ProductResponseDto.fromEntity(savedProduct);

        return productResponseDto;
    }

    public List<ProductResponseDto> findAll() {
        List<Product> products = productRepository.findAll();
        List<ProductResponseDto> productResponseDtos = products
                .stream()
                .map(product -> ProductResponseDto.fromEntity(product))
                .toList();

        return productResponseDtos;
    }
}
