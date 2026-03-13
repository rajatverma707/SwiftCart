package com.rv.admin.mapper;


import com.rv.admin.dto.ProductCategoryDto;
import com.rv.admin.entity.ProductCategoryEntity;
import org.modelmapper.ModelMapper;

public class ProductCategoryMapper {

    private static ModelMapper modelMapper = new ModelMapper();

    public static ProductCategoryDto toDto(ProductCategoryEntity productCategoryEntity) {
        return modelMapper.map(productCategoryEntity, ProductCategoryDto.class);
    }

    public static ProductCategoryEntity toEntity(ProductCategoryDto productCategoryDto) {
        return modelMapper.map(productCategoryDto, ProductCategoryEntity.class);
    }
}
