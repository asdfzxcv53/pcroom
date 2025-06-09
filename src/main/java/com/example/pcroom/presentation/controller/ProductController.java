package com.example.pcroom.presentation.controller;

import com.example.pcroom.application.ProductService;
import com.example.pcroom.presentation.ProductResponseDto;
import com.example.pcroom.presentation.ProductSaveRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(value = "/product")
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductSaveRequestDto productSaveRequestDto) {
        ProductResponseDto productResponseDto = productService.save(productSaveRequestDto);

        return ResponseEntity.ok(productResponseDto);
    }
}
