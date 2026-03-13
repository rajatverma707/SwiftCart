package com.rv.campaign.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "campaigns")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountType discountType; // PERCENTAGE or FIXED

    @Column(nullable = false)
    private Double discountValue;

    private Long productId;   // optional: apply to a specific product
    private Integer categoryId; // optional: apply to a category

    private String couponCode; // optional: for coupon based campaigns

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Column(nullable = false)
    private Boolean active;

    public enum DiscountType {
        PERCENTAGE,
        FIXED
    }
}
