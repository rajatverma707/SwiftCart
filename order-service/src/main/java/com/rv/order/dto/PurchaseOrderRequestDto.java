package com.rv.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseOrderRequestDto {
    private UserDto userDto;
    private AddressDto addressDto;
    private OrderDto orderDto;
    private List<OrderItemDto> orderItemDtoList;
}

