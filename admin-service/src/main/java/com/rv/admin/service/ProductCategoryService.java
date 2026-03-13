package com.rv.admin.service;

import java.util.List;

import com.rv.admin.dto.ProductCategoryDto;

public interface ProductCategoryService {

    public ProductCategoryDto createProductCategory(ProductCategoryDto productCategoryDto);

    public List<ProductCategoryDto> getAllProductCategories();

    public ProductCategoryDto getProductCategoryById(Integer categoryId);

    public ProductCategoryDto getProductCategoryByName(String categoryName);

    public ProductCategoryDto updateProductCategory(Integer categoryId, ProductCategoryDto productCategoryDto);

    public ProductCategoryDto deleteProductCategory(Integer categoryId, Integer updatedBy);


}
