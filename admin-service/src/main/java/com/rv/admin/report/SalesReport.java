package com.rv.admin.report;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SalesReport {

    private String customerEmail;
    private long totalOrders;
    private double totalRevenue;
    private int totalQuantity;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}
