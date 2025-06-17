package com.example.pcroom.presentation.orders;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrdersProductRequestDto {
    private Long productId;
    private int productQuantity;
}
