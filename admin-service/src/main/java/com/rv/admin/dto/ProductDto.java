package com.rv.admin.dto;

import jakarta.persistence.Column;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductDto {

    private Integer productId;
    private String name;
    private String description;
    private String title;
    private BigDecimal unitPrice;
    private String imageUrl;
    private Boolean active;
    private Integer unitsStock;
    private Integer createdBy;
    private Integer updatedBy;
    private LocalDateTime dateCreated;
    private LocalDateTime lastUpdated;
}
