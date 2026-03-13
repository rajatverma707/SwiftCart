package com.rv.inventory.controller;

import com.rv.inventory.dto.InventoryCheckRequest;
import com.rv.inventory.dto.InventoryCheckResponse;
import com.rv.inventory.dto.InventoryRequest;
import com.rv.inventory.entity.Inventory;
import com.rv.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;
    
    @PostMapping("/check")
    public InventoryCheckResponse checkAvailability(@RequestBody InventoryCheckRequest request) {
        return inventoryService.checkAvailability(request);
    }

    @GetMapping("/check/{productId}")
    public boolean checkStock(@PathVariable Long productId,
                              @RequestParam int qty) {
        return inventoryService.isInStock(productId, qty);
    }

    @PostMapping("/add")
    public Inventory add(@RequestBody InventoryRequest req) {
        return inventoryService.addStock(req.getProductId(), req.getQty());
    }

    @PostMapping("/reserve")
    public Inventory reserve(@RequestBody InventoryRequest req) {
        return inventoryService.reserve(req.getProductId(), req.getQty());
    }

    @PostMapping("/release")
    public Inventory release(@RequestBody InventoryRequest req) {
        return inventoryService.release(req.getProductId(), req.getQty());
    }

    @GetMapping("/{productId}")
    public Inventory get(@PathVariable Long productId) {
        return inventoryService.getByProductId(productId);
    }
}
