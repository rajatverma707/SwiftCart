package com.rv.inventory.dto;

import java.util.List;
import lombok.Data;

@Data
public class InventoryCheckResponse {
    private boolean available;
    private String message;
    private List<InventoryUnavailableItem> unavailableItems;
}
