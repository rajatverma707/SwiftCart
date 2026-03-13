package com.rv.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.rv.user.entity.OrderItemsEntity;


public interface OrderItemsRepository extends JpaRepository<OrderItemsEntity, Integer> {
}
