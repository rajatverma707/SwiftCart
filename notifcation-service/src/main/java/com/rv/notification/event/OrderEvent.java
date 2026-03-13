package com.rv.notification.event;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class OrderEvent {
    private OrderEventType eventType;
    private Long orderId;
    private String orderTrackingNum;
    private String customerEmail;
    private LocalDateTime eventTime;
    private List<OrderEventItem> items;
}
