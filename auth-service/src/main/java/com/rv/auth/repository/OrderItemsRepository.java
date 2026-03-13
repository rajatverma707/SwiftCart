package com.rv.auth.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.rv.auth.entity.OrderItemsEntity;


public interface OrderItemsRepository extends JpaRepository<OrderItemsEntity, Integer> {
}
