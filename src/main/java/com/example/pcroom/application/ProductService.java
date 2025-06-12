package com.example.pcroom.application;

import com.example.pcroom.domain.Product;
import com.example.pcroom.infrastructure.ProductRepository;
import com.example.pcroom.presentation.ProductResponseDto;
import com.example.pcroom.presentation.ProductSaveRequestDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponseDto save(ProductSaveRequestDto productSaveRequestDto) {
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
