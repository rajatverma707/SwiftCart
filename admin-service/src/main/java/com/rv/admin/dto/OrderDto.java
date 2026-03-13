package com.rv.admin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDto {

    private Long orderId;
    private String orderTrackingNum;
    private Integer totalQuantity;
    private BigDecimal totalPrice;
    private String orderStatus;
    private LocalDateTime deliveryDate;
    private String paymentStatus;
    private String razorpayOrderId;
    private String razorpayPaymentId;
}
