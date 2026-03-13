package com.rv.inventory.dto;

import lombok.Data;

@Data
public class InventoryUnavailableItem {
    private Long productId;
    private String productName;
    private Integer requestedQty;
    private Integer availableQty;
}
