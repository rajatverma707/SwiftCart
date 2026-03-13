package com.rv.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rv.user.entity.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByName(String name);
}
