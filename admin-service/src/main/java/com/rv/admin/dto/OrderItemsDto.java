package com.rv.admin.dto;

import jakarta.persistence.Column;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemsDto {

    private Long orderItemId;
    private String imageUrl;
    private BigDecimal unitPrice;
    private Integer quantity;
    private Long productId;
}
