package com.rv.order.mapper;

import com.rv.order.dto.UserDto;
import com.rv.order.entity.UserEntity;

import org.modelmapper.ModelMapper;

public class UserMapper {

    private static ModelMapper mapper = new ModelMapper();

    public static UserDto convertToDto(UserEntity userEntity) {
        return mapper.map(userEntity, UserDto.class);
    }

    public static UserEntity convertToEntity(UserDto userDto) {
        return mapper.map(userDto, UserEntity.class);
    }
}

