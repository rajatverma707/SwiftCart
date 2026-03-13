package com.rv.campaign.dto;

import com.rv.campaign.entity.Campaign.DiscountType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CampaignResponseDto {
    private Long id;
    private String name;
    private String description;
    private DiscountType discountType;
    private Double discountValue;
    private Long productId;
    private Integer categoryId;
    private String couponCode;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Boolean active;
}
