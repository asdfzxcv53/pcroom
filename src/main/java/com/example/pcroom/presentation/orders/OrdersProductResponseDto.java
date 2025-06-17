package com.example.pcroom.presentation.orders;

import com.example.pcroom.domain.Orders;
import com.example.pcroom.domain.OrdersProduct;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrdersProductResponseDto {

    private Long productId;
    private String productName;
    private int productPrice;
    private int productQuantity;

    public static OrdersProductResponseDto fromEntity(OrdersProduct ordersproduct) {
        OrdersProductResponseDto ordersProductResponseDto = new OrdersProductResponseDto();
        ordersProductResponseDto.productId = ordersproduct.getId();
        ordersProductResponseDto.productName = ordersproduct.getProduct().getName();
        ordersProductResponseDto.productPrice = ordersproduct.getOrderPrice();
        ordersProductResponseDto.productQuantity = ordersproduct.getOrderQuantity();

        return ordersProductResponseDto;
    }
}
