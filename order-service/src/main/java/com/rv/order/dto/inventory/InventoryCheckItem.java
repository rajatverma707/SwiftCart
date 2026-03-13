package com.rv.order.dto.inventory;

import lombok.Data;

@Data
public class InventoryCheckItem {
    private Long productId;
    private Integer quantity;
}
