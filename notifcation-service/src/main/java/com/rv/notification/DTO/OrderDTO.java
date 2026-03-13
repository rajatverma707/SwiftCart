package com.rv.notification.DTO;

public record OrderDTO(
        String item,
        int quantity,
        double price
) {
}