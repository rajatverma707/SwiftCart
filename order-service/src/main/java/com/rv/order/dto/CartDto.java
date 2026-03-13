package com.rv.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartDto {

    private Long cartId;
    private String customerEmail;
    private Integer totalQuantity;
    private BigDecimal totalPrice;
    private List<CartItemDto> items;
}
