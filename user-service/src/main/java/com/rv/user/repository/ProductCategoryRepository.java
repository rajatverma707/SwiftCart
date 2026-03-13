package com.rv.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rv.user.entity.ProductCategoryEntity;


public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Integer> {
}
