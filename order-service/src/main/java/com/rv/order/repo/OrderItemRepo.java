package com.rv.order.repo;

import com.rv.order.entity.OrderItemsEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepo extends JpaRepository<OrderItemsEntity, Integer> {
	List<OrderItemsEntity> findByOrderOrderId(Long orderId);
}

