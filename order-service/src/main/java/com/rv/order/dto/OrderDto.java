package com.rv.order.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDto {

    private Integer orderId;
    private String orderTrackingNum;
    private Double totalPrice;
    private Integer totalQuantity;
    private String orderStatus;
    private LocalDateTime deliveyDate;
    private String paymentStatus;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private LocalDateTime dateCreated;
    private LocalDateTime lastUpdated;
    private String customerEmail;
}

