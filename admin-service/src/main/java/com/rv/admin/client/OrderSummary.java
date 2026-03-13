package com.rv.admin.client;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OrderSummary {

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
