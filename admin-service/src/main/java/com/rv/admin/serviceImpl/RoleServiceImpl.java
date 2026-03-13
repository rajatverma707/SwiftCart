package com.rv.admin.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rv.admin.dto.RoleDto;
import com.rv.admin.entity.Role;
import com.rv.admin.repository.RoleRepository;
import com.rv.admin.service.RoleService;


@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    public RoleRepository repository;

    @Override
    public RoleDto create(RoleDto roleDto) {
        Role role = new Role();
        role.setRoleName(roleDto.getName());
        Role savedRole = repository.save(role);
        RoleDto createdRoleDto = new RoleDto();
        createdRoleDto.setRoleId(savedRole.getRoleId());
        createdRoleDto.setName(savedRole.getRoleName());
        return createdRoleDto;
    }

    @Override
    public RoleDto update(RoleDto role) {
        Role existing = repository.findById(role.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        existing.setRoleName(role.getName());
        Role updatedRole = repository.save(existing);
        RoleDto updatedRoleDto = new RoleDto();
        updatedRoleDto.setRoleId(updatedRole.getRoleId());
        updatedRoleDto.setName(updatedRole.getRoleName());
        return updatedRoleDto;
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public RoleDto getById(Integer id) {
        Role role = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        RoleDto roleDto = new RoleDto();
        roleDto.setRoleId(role.getRoleId());
        roleDto.setName(role.getRoleName());
        return roleDto;
    }

               
    

    @Override
    public List<RoleDto> getAll() {
        List<Role> roles = repository.findAll();
        List<RoleDto> roleDtos = new ArrayList<>();
        for (Role role : roles) {
            RoleDto roleDto = new RoleDto();
            roleDto.setRoleId(role.getRoleId());
            roleDto.setName(role.getRoleName());
            roleDtos.add(roleDto);
        }
        return roleDtos;
    }
}
