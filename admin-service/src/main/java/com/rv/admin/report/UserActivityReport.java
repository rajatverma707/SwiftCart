package com.rv.admin.report;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserActivityReport {

    private Integer userId;
    private String name;
    private String email;
    private String roleName;
    private LocalDate createdDate;
    private LocalDate updatedDate;

    private long totalOrders;
    private LocalDateTime lastOrderDate;
}
