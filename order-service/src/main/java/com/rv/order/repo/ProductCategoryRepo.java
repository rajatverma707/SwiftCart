package com.rv.order.repo;

import com.rv.order.entity.ProductCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepo extends JpaRepository<ProductCategoryEntity, Long> {
}

