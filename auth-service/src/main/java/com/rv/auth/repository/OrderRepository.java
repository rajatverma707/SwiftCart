package com.rv.auth.repository;

import com.rv.auth.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
}
