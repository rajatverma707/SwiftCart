package com.rv.inventory.dto;

import lombok.Data;

@Data
public class InventoryCheckItem {
    private Long productId;
    private Integer quantity;
}
