package com.rv.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rv.admin.dto.ApiResponse;
import com.rv.admin.dto.RoleDto;
import com.rv.admin.service.RoleService;

@RestController
@RequestMapping("/admin/roles")
public class RoleController {

    @Autowired
    public RoleService service;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<RoleDto>> create(@RequestBody RoleDto roleDto) {
        RoleDto createdRole = service.create(roleDto);
      
        ApiResponse<RoleDto> response = new ApiResponse<>();
        if (createdRole != null) {
            response.setStatusCode(201);
            response.setMessage("Role created successfully");
            response.setData(createdRole);
            return ResponseEntity.status(201).body(response);
        } else {
            response.setStatusCode(400);
            response.setMessage("Failed to create role");
            response.setData(null);
            return ResponseEntity.status(400).body(response);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<RoleDto>> update(@PathVariable Integer id, @RequestBody RoleDto roleDto) {
        RoleDto updatedRole = service.update(roleDto);
        ApiResponse<RoleDto> response = new ApiResponse<>();
        if (updatedRole != null) {
            response.setStatusCode(200);
            response.setMessage("Role updated successfully");
            response.setData(updatedRole);
            return ResponseEntity.ok(response);
        } else {
            response.setStatusCode(400);
            response.setMessage("Failed to update role");
            response.setData(null);
            return ResponseEntity.status(400).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<RoleDto>> delete(@PathVariable Integer id) {
        RoleDto deletedRole = service.getById(id);
        ApiResponse<RoleDto> response = new ApiResponse<>();
        if (deletedRole != null) {
            service.delete(id);
            response.setStatusCode(200);
            response.setMessage("Role deleted successfully");
            response.setData(deletedRole);
            return ResponseEntity.ok(response);
        } else {
            response.setStatusCode(404);
            response.setMessage("Role not found");
            response.setData(null);
            return ResponseEntity.status(404).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleDto>> getById(@PathVariable Integer id) {
        RoleDto roleDto = service.getById(id);
     
        ApiResponse<RoleDto> response = new ApiResponse<>();
        if (roleDto != null) {
            response.setStatusCode(200);
            response.setMessage("Role fetched successfully");
            response.setData(roleDto);
            return ResponseEntity.ok(response);
        } else {
            response.setStatusCode(404);
            response.setMessage("Role not found");
            response.setData(null);
            return ResponseEntity.status(404).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleDto>>> getAll() {
        List<RoleDto> roleDtos = service.getAll();
        ApiResponse<List<RoleDto>> response = new ApiResponse<>();
        response.setStatusCode(200);
        response.setMessage("Roles fetched successfully");
        response.setData(roleDtos);
        return ResponseEntity.ok(response);
    }
}
