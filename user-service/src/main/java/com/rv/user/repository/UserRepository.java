package com.rv.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rv.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);

}
