package com.rv.inventory.service;

import com.rv.inventory.dto.InventoryCheckRequest;
import com.rv.inventory.dto.InventoryCheckResponse;
import com.rv.inventory.entity.Inventory;

public interface InventoryService {
    // Check if a given quantity of a product is in stock
    boolean isInStock(Long productId, int qty);

    // Add new stock or increase existing stock for a product
    Inventory addStock(Long productId, int qty);

    // Reserve a specific quantity of stock for an order
    Inventory reserve(Long productId, int qty);

    // Release previously reserved stock, e.g., when an order is canceled
    Inventory release(Long productId, int qty);

    // Fetch the inventory details of a product by its ID
    Inventory getByProductId(Long productId);
    
    // Check availability for multiple items
    InventoryCheckResponse checkAvailability(InventoryCheckRequest request);
}
