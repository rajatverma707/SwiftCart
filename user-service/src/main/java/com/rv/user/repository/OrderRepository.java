package com.rv.user.repository;

import com.rv.user.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
}
