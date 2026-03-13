package com.rv.wishlist.repository;

import com.rv.wishlist.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    List<WishlistItem> findByCustomerEmail(String customerEmail);

    boolean existsByCustomerEmailAndProductId(String customerEmail, Long productId);

    void deleteByCustomerEmailAndProductId(String customerEmail, Long productId);
}
