package com.rv.order.service;

import com.rv.order.dto.event.OrderEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Value("${app.kafka.order-topic}")
    private String orderTopic;

    public OrderEventPublisher(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(OrderEvent event) {
        log.info("Publishing OrderEvent to topic: {} - Type: {} OrderId: {}", 
                orderTopic, event.getEventType(), event.getOrderId());
        kafkaTemplate.send(orderTopic, event.getOrderTrackingNum(), event);
        log.info("OrderEvent published successfully");
    }
}
