package com.rv.order.dto.inventory;

import java.util.List;
import lombok.Data;

@Data
public class InventoryCheckResponse {
    private boolean available;
    private String message;
    private List<InventoryUnavailableItem> unavailableItems;
}
