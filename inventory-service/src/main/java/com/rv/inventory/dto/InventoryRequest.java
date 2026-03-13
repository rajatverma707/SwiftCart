package com.rv.inventory.dto;

import lombok.Data;

@Data
public class InventoryRequest {
    private Long productId;
    private int qty;
}
