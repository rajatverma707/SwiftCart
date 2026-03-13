package com.rv.order.dto.event;

import lombok.Data;

@Data
public class OrderEventItem {
    private Long productId;
    private Integer quantity;
}
