package com.rv.inventory.event;

import com.rv.inventory.entity.Inventory;
import com.rv.inventory.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Value("${app.kafka.order-topic:order.events}")
    private String orderTopic;

    @KafkaListener(topics = "order.events", groupId = "inventory-group")
    public void consume(OrderEvent event) {
        if (event == null || event.getEventType() == null) {
            log.warn("Received null or invalid OrderEvent payload; ignoring");
            return;
        }
        
        log.info("Received order event: type={} orderId={} orderTrackingNum={}",
                event.getEventType(), event.getOrderId(), event.getOrderTrackingNum());
        
        if (event.getEventType() == OrderEventType.ORDER_CREATED) {
            handleOrderCreated(event);
        } else if (event.getEventType() == OrderEventType.ORDER_CANCELLED) {
            handleOrderCancelled(event);
        } else {
            log.warn("Unknown event type: {}", event.getEventType());
        }
    }
    
    private void handleOrderCreated(OrderEvent event) {
        if (event.getItems() == null || event.getItems().isEmpty()) {
            log.warn("Order created event has no items; orderId={}", event.getOrderId());
            publishInventoryReservationFailed(event);
            return;
        }

        boolean canReserveAll = true;

        // First pass: validate inventory for all items (all-or-nothing check)
        for (OrderEventItem item : event.getItems()) {
            if (item.getProductId() == null || item.getQuantity() <= 0) {
                log.warn("Invalid item: productId={} quantity={}", item.getProductId(), item.getQuantity());
                canReserveAll = false;
                break;
            }
            
            Inventory inventory = inventoryRepository.findById(item.getProductId()).orElse(null);
            if (inventory == null) {
                log.error("Inventory not found for productId={}", item.getProductId());
                canReserveAll = false;
                break;
            }
            
            int available = inventory.getAvailableQuantity() != null ? inventory.getAvailableQuantity() : 0;
            if (available < item.getQuantity()) {
                log.error("Insufficient stock for productId={} requested={} available={}",
                        item.getProductId(), item.getQuantity(), available);
                canReserveAll = false;
                break;
            }
        }

        if (!canReserveAll) {
            log.warn("Inventory reservation failed for orderId={}; rolling back and publishing failure event", event.getOrderId());
            publishInventoryReservationFailed(event);
            return;
        }

        // Second pass: perform reservations now that we know stock is sufficient for all items
        for (OrderEventItem item : event.getItems()) {
            Inventory inventory = inventoryRepository.findById(item.getProductId()).orElse(null);

            int available = inventory.getAvailableQuantity() != null ? inventory.getAvailableQuantity() : 0;
            int reserved = inventory.getReservedQuantity() != null ? inventory.getReservedQuantity() : 0;

            inventory.setAvailableQuantity(available - item.getQuantity());
            inventory.setReservedQuantity(reserved + item.getQuantity());
            inventoryRepository.save(inventory);

            log.info("Inventory reserved for orderId={} productId={} quantity={} available={} reserved={}",
                    event.getOrderId(), item.getProductId(), item.getQuantity(),
                    inventory.getAvailableQuantity(), inventory.getReservedQuantity());
        }

        publishInventoryReserved(event);
    }
    
    private void handleOrderCancelled(OrderEvent event) {
        if (event.getItems() == null || event.getItems().isEmpty()) {
            log.warn("Order cancelled event has no items; orderId={}", event.getOrderId());
            return;
        }
        
        for (OrderEventItem item : event.getItems()) {
            if (item.getProductId() == null || item.getQuantity() <= 0) {
                log.warn("Invalid item: productId={} quantity={}", item.getProductId(), item.getQuantity());
                continue;
            }
            
            Inventory inventory = inventoryRepository.findById(item.getProductId()).orElse(null);
            if (inventory == null) {
                log.warn("Inventory not found for productId={} - cannot release stock", item.getProductId());
                continue;
            }
            
            int available = inventory.getAvailableQuantity() != null ? inventory.getAvailableQuantity() : 0;
            int reserved = inventory.getReservedQuantity() != null ? inventory.getReservedQuantity() : 0;
            
            // Release inventory: move from reserved back to available
            inventory.setAvailableQuantity(available + item.getQuantity());
            inventory.setReservedQuantity(Math.max(0, reserved - item.getQuantity()));
            inventoryRepository.save(inventory);
            
            log.info("Inventory released for orderId={} productId={} quantity={} available={} reserved={}",
                    event.getOrderId(), item.getProductId(), item.getQuantity(),
                    inventory.getAvailableQuantity(), inventory.getReservedQuantity());
        }
    }

    private void publishInventoryReserved(OrderEvent sourceEvent) {
        OrderEvent event = new OrderEvent();
        event.setEventType(OrderEventType.INVENTORY_RESERVED);
        event.setOrderId(sourceEvent.getOrderId());
        event.setOrderTrackingNum(sourceEvent.getOrderTrackingNum());
        event.setCustomerEmail(sourceEvent.getCustomerEmail());
        event.setEventTime(sourceEvent.getEventTime());
        event.setItems(sourceEvent.getItems());

        log.info("Publishing INVENTORY_RESERVED event for orderId={} trackingNum={}",
                event.getOrderId(), event.getOrderTrackingNum());
        kafkaTemplate.send(orderTopic, event.getOrderTrackingNum(), event);
    }

    private void publishInventoryReservationFailed(OrderEvent sourceEvent) {
        OrderEvent event = new OrderEvent();
        event.setEventType(OrderEventType.INVENTORY_RESERVATION_FAILED);
        event.setOrderId(sourceEvent.getOrderId());
        event.setOrderTrackingNum(sourceEvent.getOrderTrackingNum());
        event.setCustomerEmail(sourceEvent.getCustomerEmail());
        event.setEventTime(sourceEvent.getEventTime());
        event.setItems(sourceEvent.getItems());

        log.info("Publishing INVENTORY_RESERVATION_FAILED event for orderId={} trackingNum={}",
                event.getOrderId(), event.getOrderTrackingNum());
        kafkaTemplate.send(orderTopic, event.getOrderTrackingNum(), event);
    }
}
