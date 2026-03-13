package com.rv.order.repo;

import com.rv.order.entity.ShippingAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShippingAddressRepo extends JpaRepository<ShippingAddressEntity, Integer> {

    public List<ShippingAddressEntity> findByUserUserId(Integer userId);
}

