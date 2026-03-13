package com.rv.order.service;

import com.rv.order.dto.event.OrderEvent;
import com.rv.order.dto.event.OrderEventType;
import com.rv.order.entity.OrderEntity;
import com.rv.order.repo.OrderRepo;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderSagaListener {

    private static final Logger log = LoggerFactory.getLogger(OrderSagaListener.class);

    private final OrderRepo orderRepo;

    public OrderSagaListener(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }

    @KafkaListener(
            topics = "${app.kafka.order-topic}",
            groupId = "order-saga-group"
    )
    public void onSagaEvent(OrderEvent event, ConsumerRecord<String, OrderEvent> record) {
        if (event == null || event.getEventType() == null) {
            log.warn("Received null or invalid OrderEvent in saga listener; ignoring");
            return;
        }

        log.info("OrderSagaListener received event: type={} orderId={} trackingNum={} partition={} offset={}",
                event.getEventType(), event.getOrderId(), event.getOrderTrackingNum(),
                record.partition(), record.offset());

        if (event.getEventType() == OrderEventType.INVENTORY_RESERVED) {
            handleInventoryReserved(event);
        } else if (event.getEventType() == OrderEventType.INVENTORY_RESERVATION_FAILED) {
            handleInventoryReservationFailed(event);
        } else {
            // Ignore other event types; they are handled elsewhere
            log.debug("OrderSagaListener ignoring event type: {}", event.getEventType());
        }
    }

    private void handleInventoryReserved(OrderEvent event) {
        OrderEntity order = findOrder(event);
        if (order == null) {
            return;
        }

        // Inventory is successfully reserved; keep status as CREATED/PENDING and log for traceability
        log.info("Inventory reserved for orderId={} trackingNum={}; order status remains {} paymentStatus {}",
                order.getOrderId(), order.getOrderTrackingNum(),
                order.getOrderStatus(), order.getPaymentStatus());
    }

    private void handleInventoryReservationFailed(OrderEvent event) {
        OrderEntity order = findOrder(event);
        if (order == null) {
            return;
        }

        // Compensating action: mark order as cancelled due to inventory failure
        order.setOrderStatus("CANCELLED");
        // Payment has not been captured yet in this flow, mark as FAILED/PENDING appropriately
        if (order.getPaymentStatus() == null || order.getPaymentStatus().isBlank()) {
            order.setPaymentStatus("FAILED");
        }
        order.setDeliveryDate(null);
        orderRepo.save(order);

        log.info("Order cancelled due to inventory reservation failure - orderId={} trackingNum={}",
                order.getOrderId(), order.getOrderTrackingNum());
    }

    private OrderEntity findOrder(OrderEvent event) {
        OrderEntity order = null;
        if (event.getOrderId() != null) {
            // OrderRepo is keyed by Integer, but our event carries Long, so convert safely
            Integer id = event.getOrderId().intValue();
            order = orderRepo.findById(id).orElse(null);
        }
        if (order == null && event.getOrderTrackingNum() != null) {
            order = orderRepo.findByOrderTrackingNum(event.getOrderTrackingNum());
        }

        if (order == null) {
            log.warn("Order not found for saga event - orderId={} trackingNum={}",
                    event.getOrderId(), event.getOrderTrackingNum());
        }
        return order;
    }
}
