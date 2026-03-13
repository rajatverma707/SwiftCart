package com.rv.order.repo;

import com.rv.order.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepo extends JpaRepository<CartEntity, Long> {

    CartEntity findByCustomerEmail(String customerEmail);
}
