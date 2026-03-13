package com.rv.inventory.dto;

import java.util.List;
import lombok.Data;

@Data
public class InventoryCheckRequest {
    private List<InventoryCheckItem> items;
}
