package com.rv.auth.mapper;

import com.rv.auth.dto.ShippingAddressDto;
import com.rv.auth.entity.ShippingAddressEntity;
import org.modelmapper.ModelMapper;

public class AddressMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    public static ShippingAddressEntity dtoToEntity(ShippingAddressDto shippingAddressDto) {
        return modelMapper.map(shippingAddressDto, ShippingAddressEntity.class);
    }

    public static ShippingAddressDto entityToDto(ShippingAddressEntity shippingAddressEntity) {
        return modelMapper.map(shippingAddressEntity, ShippingAddressDto.class);
    }
}
