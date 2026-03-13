package com.rv.order.repo;

import com.rv.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepo extends JpaRepository<OrderEntity, Integer> {

    public OrderEntity findByOrderTrackingNum(String orderTrackingNum);

    public List<OrderEntity> findByUserEmail(String email);

}

