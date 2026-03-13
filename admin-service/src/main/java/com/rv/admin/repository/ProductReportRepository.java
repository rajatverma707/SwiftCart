package com.rv.admin.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rv.admin.entity.ProductEntity;
import com.rv.admin.report.InventoryReportView;

public interface ProductReportRepository extends JpaRepository<ProductEntity, Integer> {

    @Query("""
        SELECT 
            p.productId AS productId,
            p.name AS productName,
            c.categoryName AS categoryName,
            p.unitsStock AS stock,
            p.active AS active
        FROM product p
        JOIN p.category c
        WHERE (:categoryId IS NULL OR c.categoryId = :categoryId)
          AND (:active IS NULL OR p.active = :active)
          AND (:minStock IS NULL OR p.unitsStock >= :minStock)
          AND (:maxStock IS NULL OR p.unitsStock <= :maxStock)
          AND (:fromDate IS NULL OR p.dateCreated >= :fromDate)
          AND (:toDate IS NULL OR p.dateCreated <= :toDate)
    """)
    List<InventoryReportView> getInventoryReport(
            @Param("categoryId") Integer categoryId,
            @Param("active") Boolean active,
            @Param("minStock") Integer minStock,
            @Param("maxStock") Integer maxStock,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);
}

