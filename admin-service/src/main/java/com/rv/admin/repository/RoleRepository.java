package com.rv.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rv.admin.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
