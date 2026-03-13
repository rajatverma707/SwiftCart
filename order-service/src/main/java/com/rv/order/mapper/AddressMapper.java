package com.rv.order.mapper;

import com.rv.order.dto.AddressDto;
import com.rv.order.entity.ShippingAddressEntity;
import org.modelmapper.ModelMapper;

public class AddressMapper {

    private static ModelMapper mapper = new ModelMapper();

    public static AddressDto convertToDto(ShippingAddressEntity addrEntity) {
        return mapper.map(addrEntity, AddressDto.class);
    }

    public static ShippingAddressEntity toEntity(AddressDto addrDto) {
        return mapper.map(addrDto, ShippingAddressEntity.class);
    }
}

