package com.rv.auth.repository;

import com.rv.auth.entity.ShippingAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ShippingAddressRepository extends JpaRepository<ShippingAddressEntity, Long> {
}
