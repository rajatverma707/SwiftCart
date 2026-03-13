package com.rv.admin.report;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CampaignReportItem {

    private Long id;
    private String name;
    private String description;
    private String discountType;
    private Double discountValue;
    private Long productId;
    private Integer categoryId;
    private String couponCode;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Boolean active;
}
