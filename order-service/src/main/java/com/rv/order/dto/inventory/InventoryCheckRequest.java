package com.rv.order.dto.inventory;

import java.util.List;
import lombok.Data;

@Data
public class InventoryCheckRequest {
    private List<InventoryCheckItem> items;
}
