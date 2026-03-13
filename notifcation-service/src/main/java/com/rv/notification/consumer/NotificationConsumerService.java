package com.rv.notification.consumer;

import com.rv.notification.event.OrderEvent;
import com.rv.notification.event.OrderEventType;
import com.rv.notification.entity.Notification;
import com.rv.notification.repository.NotificationRepository;
import com.rv.notification.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Service
public class NotificationConsumerService {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumerService.class);

    @Autowired
    protected NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "order.events", groupId = "notification-group")
    public void listen(OrderEvent event) {
        if (event == null || event.getEventType() == null) {
            log.warn("Received null or invalid OrderEvent; ignoring");
            return;
        }
        
        log.info("Received order event: type={} orderId={} orderTrackingNum={} customerEmail={}",
                event.getEventType(), event.getOrderId(), event.getOrderTrackingNum(), event.getCustomerEmail());
        
        Notification notification = new Notification();
        notification.setOrderId(event.getOrderId());
        notification.setOrderTrackingNum(event.getOrderTrackingNum());
        notification.setCustomerEmail(event.getCustomerEmail());
        notification.setType(event.getEventType().toString());
        notification.setNotificationChannel("EMAIL");
        notification.setRetryCount(0);

        try {
            if (event.getEventType() == OrderEventType.ORDER_CREATED) {
                sendOrderCreatedEmail(event);
                notification.setStatus("SENT");
                notification.setSentAt(LocalDateTime.now());
            } else if (event.getEventType() == OrderEventType.ORDER_CANCELLED) {
                sendOrderCancelledEmail(event);
                notification.setStatus("SENT");
                notification.setSentAt(LocalDateTime.now());
            } else if (event.getEventType() == OrderEventType.INVENTORY_RESERVATION_FAILED) {
                sendInventoryFailureEmail(event);
                notification.setStatus("SENT");
                notification.setSentAt(LocalDateTime.now());
            }
        } catch (Exception e) {
            log.error("Failed to send notification for order {}", event.getOrderId(), e);
            notification.setStatus("FAILED");
            notification.setFailureReason(e.getMessage());
            notification.setRetryCount(notification.getRetryCount() + 1);
        }

        notificationRepository.save(notification);
        log.info("Notification saved - orderId: {} status: {}", event.getOrderId(), notification.getStatus());
    }
    
    private void sendOrderCreatedEmail(OrderEvent event) {
        emailService.sendOrderCreatedEmail(event);
        log.info("[EMAIL] Order Created - Sent to: {} OrderID: {} TrackingNum: {}",
                event.getCustomerEmail(), event.getOrderId(), event.getOrderTrackingNum());
    }
    
    private void sendOrderCancelledEmail(OrderEvent event) {
        emailService.sendOrderCancelledEmail(event);
        log.info("[EMAIL] Order Cancelled - Sent to: {} OrderID: {} TrackingNum: {}",
                event.getCustomerEmail(), event.getOrderId(), event.getOrderTrackingNum());
    }

    private void sendInventoryFailureEmail(OrderEvent event) {
        emailService.sendInventoryFailureEmail(event);
        log.info("[EMAIL] Inventory Failure - Sent to: {} OrderID: {} TrackingNum: {}",
                event.getCustomerEmail(), event.getOrderId(), event.getOrderTrackingNum());
    }
}

