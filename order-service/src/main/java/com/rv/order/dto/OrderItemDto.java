package com.rv.order.dto;

import lombok.Data;

@Data
public class OrderItemDto {

    private Integer itemId;
    private Long productId;
    private String imageUrl;
    private Integer quantity;
    private Double unitPrice;
    private String productName;
}

