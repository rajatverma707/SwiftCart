package com.rv.inventory.service;

import com.rv.inventory.dto.InventoryCheckItem;
import com.rv.inventory.dto.InventoryCheckRequest;
import com.rv.inventory.dto.InventoryCheckResponse;
import com.rv.inventory.dto.InventoryUnavailableItem;
import com.rv.inventory.entity.Inventory;
import com.rv.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final InventoryRepository repository;

    public InventoryServiceImpl(InventoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isInStock(Long productId, int qty) {
        Inventory inv = repository.findById(productId).orElse(null);
        if (inv == null || inv.getAvailableQuantity() == null) {
            return false;
        }
        return inv.getAvailableQuantity() >= qty;
    }

    @Override
    public Inventory addStock(Long productId, int qty) {
        Inventory inv = repository.findById(productId).orElseGet(() -> {
            Inventory newInv = new Inventory();
            newInv.setProductId(productId);
            newInv.setProductName("Product-" + productId); // TODO: Get from product service
            newInv.setSku("SKU-" + productId); // TODO: Get from product service
            newInv.setAvailableQuantity(0);
            newInv.setReservedQuantity(0);
            newInv.setTotalQuantity(0);
            newInv.setReorderLevel(50);
            newInv.setWarehouseLocation("MAIN");
            return newInv;
        });
        
        int currentAvailable = inv.getAvailableQuantity() != null ? inv.getAvailableQuantity() : 0;
        int addQty = Math.max(qty, 0);
        inv.setAvailableQuantity(currentAvailable + addQty);
        inv.setLastRestockDate(LocalDateTime.now());
        
        // Update total quantity
        int reserved = inv.getReservedQuantity() != null ? inv.getReservedQuantity() : 0;
        inv.setTotalQuantity(inv.getAvailableQuantity() + reserved);
        
        log.info("Added stock for productId={} qty={} totalQty={}",
                productId, addQty, inv.getTotalQuantity());
        return repository.save(inv);
    }

    @Override
    public Inventory reserve(Long productId, int qty) {
        Inventory inv = repository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Inventory not found for productId=" + productId));
        int available = inv.getAvailableQuantity() != null ? inv.getAvailableQuantity() : 0;
        int reserved = inv.getReservedQuantity() != null ? inv.getReservedQuantity() : 0;
        
        if (qty <= 0 || available < qty) {
            throw new IllegalStateException("Insufficient inventory for productId=" + productId + 
                    " requested=" + qty + " available=" + available);
        }
        
        inv.setAvailableQuantity(available - qty);
        inv.setReservedQuantity(reserved + qty);
        
        log.info("Reserved inventory for productId={} qty={} available={} reserved={}",
                productId, qty, inv.getAvailableQuantity(), inv.getReservedQuantity());
        return repository.save(inv);
    }

    @Override
    public Inventory release(Long productId, int qty) {
        Inventory inv = repository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Inventory not found for productId=" + productId));
        int available = inv.getAvailableQuantity() != null ? inv.getAvailableQuantity() : 0;
        int reserved = inv.getReservedQuantity() != null ? inv.getReservedQuantity() : 0;
        
        int releaseQty = Math.max(qty, 0);
        inv.setAvailableQuantity(available + releaseQty);
        inv.setReservedQuantity(Math.max(0, reserved - releaseQty));
        
        log.info("Released inventory for productId={} qty={} available={} reserved={}",
                productId, releaseQty, inv.getAvailableQuantity(), inv.getReservedQuantity());
        return repository.save(inv);
    }

    @Override
    public Inventory getByProductId(Long productId) {
        return repository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Inventory not found for productId=" + productId));
    }
    
    @Override
    public InventoryCheckResponse checkAvailability(InventoryCheckRequest request) {
        InventoryCheckResponse response = new InventoryCheckResponse();
        List<InventoryUnavailableItem> unavailableItems = new ArrayList<>();
        
        for (InventoryCheckItem item : request.getItems()) {
            if (!isInStock(item.getProductId(), item.getQuantity())) {
                InventoryUnavailableItem unavailable = new InventoryUnavailableItem();
                unavailable.setProductId(item.getProductId());
                unavailable.setRequestedQty(item.getQuantity());

                Inventory inv = repository.findById(item.getProductId()).orElse(null);
                if (inv != null) {
                    unavailable.setAvailableQty(inv.getAvailableQuantity());
                    unavailable.setProductName(inv.getProductName());
                } else {
                    unavailable.setAvailableQty(0);
                    unavailable.setProductName(null);
                }
                unavailableItems.add(unavailable);
            }
        }
        
        if (unavailableItems.isEmpty()) {
            response.setAvailable(true);
            response.setMessage("All items are in stock");
        } else {
            response.setAvailable(false);
            response.setMessage("Some items are not available");
            response.setUnavailableItems(unavailableItems);
        }
        
        return response;
    }
}
