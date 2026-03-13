package com.rv.admin.serviceImpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rv.admin.dto.ProductCategoryDto;
import com.rv.admin.entity.ProductCategoryEntity;
import com.rv.admin.mapper.ProductCategoryMapper;
import com.rv.admin.repository.ProductCategoryRepository;
import com.rv.admin.service.ProductCategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Override
    public ProductCategoryDto createProductCategory(ProductCategoryDto productCategoryDto) {

        productCategoryDto.setActiveSw("Y");
        productCategoryDto.setCreatedBy(productCategoryDto.getCreatedBy());

        ProductCategoryEntity productCategoryEntity = ProductCategoryMapper.toEntity(productCategoryDto);

        ProductCategoryEntity savedEntity = productCategoryRepository.save(productCategoryEntity);

        return ProductCategoryMapper.toDto(savedEntity);

    }

    @Override
    public List<ProductCategoryDto> getAllProductCategories() {

        List<ProductCategoryEntity> entities = productCategoryRepository.findAll();

        return entities.stream().map(ProductCategoryMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ProductCategoryDto getProductCategoryById(Integer categoryId) {

        ProductCategoryEntity productCategoryEntity = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Product category not found with id: " + categoryId));

        return ProductCategoryMapper.toDto(productCategoryEntity);
    }

    public ProductCategoryDto getProductCategoryByName(String categoryName) {

        ProductCategoryEntity productCategoryEntity = productCategoryRepository.findByCategoryName(categoryName);

        if (productCategoryEntity != null) {
            return ProductCategoryMapper.toDto(productCategoryEntity);
        }

        return null;
    }

    @Override
    public ProductCategoryDto updateProductCategory(Integer categoryId, ProductCategoryDto productCategoryDto) {

        ProductCategoryEntity existingEntity = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Product category not found with id: " + categoryId));

        existingEntity.setCategoryName(productCategoryDto.getCategoryName());
        existingEntity.setCategoryDescription(productCategoryDto.getCategoryDescription());
        existingEntity.setActiveSw(productCategoryDto.getActiveSw());
        existingEntity.setUpdatedBy(Integer.parseInt(productCategoryDto.getUpdatedBy()));

        ProductCategoryEntity updatedEntity = productCategoryRepository.save(existingEntity); //UPSERT

        return ProductCategoryMapper.toDto(updatedEntity);

    }

    @Override
    public ProductCategoryDto deleteProductCategory(Integer categoryId, Integer updatedBy) {

        ProductCategoryEntity existingEntity = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Product category not found with id: " + categoryId));

        // productCategoryRepository.delete(existingEntity);

        existingEntity.setActiveSw("N");
        existingEntity.setUpdatedBy(updatedBy);
        ProductCategoryEntity updatedEntity = productCategoryRepository.save(existingEntity);//UPSERT

        return ProductCategoryMapper.toDto(updatedEntity);

    }

}