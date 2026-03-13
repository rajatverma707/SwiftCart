package com.rv.admin.mapper;

import com.rv.admin.dto.RoleDto;
import com.rv.admin.entity.Role;

public class RoleMapper {
    public static RoleDto toDto(Role role) {
        RoleDto dto = new RoleDto();
        dto.setRoleId(role.getRoleId());
        dto.setName(role.getRoleName());
        // Add other fields as needed
        return dto;
    }

    public static Role toEntity(RoleDto dto) {
        Role role = new Role();
        role.setRoleId(dto.getRoleId());
        role.setRoleName(dto.getName());
        // Add other fields as needed
        return role;
    }
}
