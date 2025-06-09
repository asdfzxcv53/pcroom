package com.example.pcroom.presentation;

import com.example.pcroom.domain.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSaveRequestDto {

    private String name;

    private int price;

    private int quantity;

    public Product toEntity() {
        Product product = Product.builder()
                .name(name)
                .price(price)
                .quantity(quantity).build();

        return product;
    }
}
