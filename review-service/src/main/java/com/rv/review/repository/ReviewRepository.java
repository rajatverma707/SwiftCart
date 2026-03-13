package com.rv.review.repository;

import com.rv.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductId(Long productId);

    List<Review> findByCustomerEmail(String customerEmail);

    boolean existsByCustomerEmailAndProductId(String customerEmail, Long productId);
}
