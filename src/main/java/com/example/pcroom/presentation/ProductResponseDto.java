package com.example.pcroom.presentation;

import com.example.pcroom.domain.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductResponseDto {

    private Long id;
    private String name;
    private int price;
    private int quantity;

    public static ProductResponseDto fromEntity(Product product) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.id = product.getId();
        dto.name = product.getName();
        dto.price = product.getPrice();
        dto.quantity = product.getQuantity();

        return dto;
    }
}
