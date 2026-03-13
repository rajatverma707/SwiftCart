package com.rv.admin.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.rv.admin.entity.ProductEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

  
	 List<ProductEntity> findAllByNameContainingIgnoreCase(String productName);

    @Query("""
        SELECT p FROM product p
        WHERE (:categoryId IS NULL OR p.category.categoryId = :categoryId)
          AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:minPrice IS NULL OR p.unitPrice >= :minPrice)
          AND (:maxPrice IS NULL OR p.unitPrice <= :maxPrice)
    """)
    List<ProductEntity> filterProducts(
            @Param("categoryId") Integer categoryId,
            @Param("search") String search,
            @Param("minPrice") java.math.BigDecimal minPrice,
            @Param("maxPrice") java.math.BigDecimal maxPrice
    );
}
