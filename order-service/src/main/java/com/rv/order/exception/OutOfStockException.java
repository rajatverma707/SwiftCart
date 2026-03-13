package com.rv.order.exception;

import com.rv.order.dto.inventory.InventoryUnavailableItem;
import java.util.List;

public class OutOfStockException extends RuntimeException {

    private final List<InventoryUnavailableItem> unavailableItems;

    public OutOfStockException(String message, List<InventoryUnavailableItem> unavailableItems) {
        super(message);
        this.unavailableItems = unavailableItems;
    }

    public List<InventoryUnavailableItem> getUnavailableItems() {
        return unavailableItems;
    }
}
