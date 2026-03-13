package com.rv.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rv.admin.entity.ProductCategoryEntity;

public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Integer> {

    ProductCategoryEntity findByCategoryName(String categoryName);
 
}
