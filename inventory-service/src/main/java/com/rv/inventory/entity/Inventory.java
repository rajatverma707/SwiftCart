package com.rv.inventory.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    private Long productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private Integer totalQuantity = 0;

    @Column(nullable = false)
    private Integer availableQuantity = 0;

    @Column(nullable = false)
    private Integer reservedQuantity = 0; // Items in pending orders

    @Column(nullable = false)
    private Integer reorderLevel = 50; // Minimum threshold for auto-reorder

    @Column(name = "warehouse_location")
    private String warehouseLocation = "MAIN"; // MAIN, BRANCH1, BRANCH2, etc.

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime lastUpdatedAt;

    private LocalDateTime lastRestockDate;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUpdatedAt = LocalDateTime.now();
        if (availableQuantity == null) {
            availableQuantity = 0;
        }
        if (reservedQuantity == null) {
            reservedQuantity = 0;
        }
        if (totalQuantity == null) {
            totalQuantity = availableQuantity + reservedQuantity;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdatedAt = LocalDateTime.now();
        // Ensure totalQuantity = availableQuantity + reservedQuantity
        if (totalQuantity == null) {
            totalQuantity = (availableQuantity != null ? availableQuantity : 0) + 
                           (reservedQuantity != null ? reservedQuantity : 0);
        }
    }
}
