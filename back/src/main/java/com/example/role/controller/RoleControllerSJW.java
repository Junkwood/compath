package com.example.role.controller;

import com.example.role.dto.PermissionDTOSJW;
import com.example.role.dto.RoleDTOSJW;
import com.example.role.service.RoleServiceSJW;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class RoleControllerSJW {
    private final RoleServiceSJW roleService;

    @GetMapping("/api/role/list")
    public List<RoleDTOSJW> getAllRoles() {
        return roleService.getAll();
    }
    @GetMapping("/api/permission/list")
    public List<PermissionDTOSJW> getAllPermission() {
        return roleService.getAllPermission();
    }
    @PostMapping("/api/role")
    public RoleDTOSJW registerRole(@RequestBody RoleDTOSJW role) {
        return roleService.registerRole(role);
    }
    @PutMapping("/api/role")
        public RoleDTOSJW modifyRole(@RequestBody RoleDTOSJW role) {
        return roleService.modifyRole(role);
    }
    @DeleteMapping("/api/role")
    public Boolean deleteRole(Integer roleId) {
        return roleService.deleteRole(roleId);
    }
}