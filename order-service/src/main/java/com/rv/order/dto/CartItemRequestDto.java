package com.rv.order.dto;

import lombok.Data;

@Data
public class CartItemRequestDto {

    private String customerEmail;
    private Long productId;
    private Integer quantity;
}
