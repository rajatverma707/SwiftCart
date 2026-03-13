package com.rv.order.mapper;

import com.rv.order.dto.OrderDto;
import com.rv.order.entity.OrderEntity;
import org.modelmapper.ModelMapper;

public class OrderMapper {

    private static ModelMapper mapper = new ModelMapper();

    public static OrderDto convertToDto(OrderEntity orderEntity) {
        OrderDto dto = mapper.map(orderEntity, OrderDto.class);
        if (orderEntity.getUser() != null) {
            dto.setCustomerEmail(orderEntity.getUser().getEmail());
        }
        return dto;
    }

    public static OrderEntity convertToEntity(OrderDto orderDto) {
        return mapper.map(orderDto, OrderEntity.class);
    }
}

