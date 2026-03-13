package com.rv.order.dto;

import lombok.Data;

@Data
public class CartCheckoutRequestDto {

    private UserDto userDto;
    private AddressDto addressDto;
}
