package com.rv.campaign.dto;

import lombok.Data;

@Data
public class PriceCalculationResponseDto {
    private Double originalPrice;
    private Double discountedPrice;
    private String campaignName;
    private String couponCode;
}
