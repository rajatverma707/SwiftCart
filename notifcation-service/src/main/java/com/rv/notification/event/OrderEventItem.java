package com.rv.notification.event;

import lombok.Data;

@Data
public class OrderEventItem {
    private Long productId;
    private Integer quantity;
}
