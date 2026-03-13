package com.rv.admin.mapper;

import org.modelmapper.ModelMapper;

import com.rv.admin.dto.ProductDto;
import com.rv.admin.entity.ProductEntity;

public class ProductMapper {

    private static ModelMapper modelMapper = new ModelMapper();

    public static ProductDto toDto(ProductEntity productEntity) {
        return modelMapper.map(productEntity, ProductDto.class);
    }

    public static ProductEntity toEntity(ProductDto productDto) {
        return modelMapper.map(productDto, ProductEntity.class);
    }
}
