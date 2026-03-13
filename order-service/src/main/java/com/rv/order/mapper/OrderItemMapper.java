package com.rv.order.mapper;

import com.rv.order.dto.OrderItemDto;
import com.rv.order.entity.OrderItemsEntity;
import java.math.BigDecimal;

public class OrderItemMapper {

    public static OrderItemDto convertToDto(OrderItemsEntity orderItemEntity) {
        if (orderItemEntity == null) {
            return null;
        }
        OrderItemDto dto = new OrderItemDto();
        dto.setItemId(orderItemEntity.getOrderItemId() != null ? orderItemEntity.getOrderItemId().intValue() : null);
        dto.setProductId(orderItemEntity.getProductId());
        dto.setImageUrl(orderItemEntity.getImageUrl());
        dto.setQuantity(orderItemEntity.getQuantity());
        dto.setUnitPrice(orderItemEntity.getUnitPrice() != null ? orderItemEntity.getUnitPrice().doubleValue() : null);
        return dto;
    }

    public static OrderItemsEntity convertToEntity(OrderItemDto orderItemDto) {
        if (orderItemDto == null) {
            return null;
        }
        OrderItemsEntity entity = new OrderItemsEntity();
        if (orderItemDto.getItemId() != null) {
            entity.setOrderItemId(Long.valueOf(orderItemDto.getItemId()));
        }
        entity.setProductId(orderItemDto.getProductId());
        entity.setImageUrl(orderItemDto.getImageUrl());
        entity.setQuantity(orderItemDto.getQuantity());
        entity.setUnitPrice(orderItemDto.getUnitPrice() != null ? BigDecimal.valueOf(orderItemDto.getUnitPrice()) : null);
        return entity;
    }
}

