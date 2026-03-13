package com.rv.user.repository;

import com.rv.user.entity.ShippingAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddressEntity, Long> {
	@Query("SELECT s FROM ShippingAddressEntity s WHERE s.user.id = :userId AND LOWER(s.addrType) = LOWER(:addrType)")
	Optional<ShippingAddressEntity> findByUserIdAndAddrType(@Param("userId") Integer userId, @Param("addrType") String addrType);
}
