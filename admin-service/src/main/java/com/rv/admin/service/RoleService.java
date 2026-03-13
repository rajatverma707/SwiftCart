package com.rv.admin.service;

import java.util.List;

import com.rv.admin.dto.RoleDto;

public interface RoleService {

    RoleDto create(RoleDto RoleDto);

    RoleDto update(RoleDto RoleDto);

    void delete(Integer id);

    RoleDto getById(Integer id);

    List<RoleDto> getAll();

}
