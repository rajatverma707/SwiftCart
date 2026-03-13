package com.rv.order.repo;

import com.rv.order.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<UserEntity, Integer> {

    public UserEntity findByEmailAndPassword(String email, String pwd);

    public UserEntity findByEmail(String email);

}

