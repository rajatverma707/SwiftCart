package com.rv.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rv.auth.entity.ProductCategoryEntity;


public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Integer> {
}
