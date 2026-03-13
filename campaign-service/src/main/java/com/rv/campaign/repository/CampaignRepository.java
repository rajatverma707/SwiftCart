package com.rv.campaign.repository;

import com.rv.campaign.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    @Query("""
            SELECT c FROM Campaign c
            WHERE c.active = true
              AND c.startAt <= :now
              AND c.endAt >= :now
              AND (:productId IS NULL OR c.productId = :productId)
              AND (:categoryId IS NULL OR c.categoryId = :categoryId)
            """)
    List<Campaign> findActiveCampaigns(@Param("now") LocalDateTime now,
                                       @Param("productId") Long productId,
                                       @Param("categoryId") Integer categoryId);

    Optional<Campaign> findByCouponCodeAndActiveTrue(String couponCode);
}
