package com.rv.review.dto;

import lombok.Data;

@Data
public class OrderSummaryDto {

    private String orderTrackingNum;
    private String customerEmail;
    private String orderStatus;
    private String paymentStatus;
}
