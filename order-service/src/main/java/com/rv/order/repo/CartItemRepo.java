package com.rv.order.repo;

import com.rv.order.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepo extends JpaRepository<CartItemEntity, Long> {

    List<CartItemEntity> findByCartCartId(Long cartId);

    CartItemEntity findByCartCartIdAndProductId(Long cartId, Long productId);

    void deleteByCartCartId(Long cartId);
}
