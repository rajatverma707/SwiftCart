package com.rv.campaign.dto;

import lombok.Data;

@Data
public class PriceCalculationRequestDto {
    private Long productId;
    private Integer categoryId;
    private Double originalPrice;
    private String couponCode;
}
