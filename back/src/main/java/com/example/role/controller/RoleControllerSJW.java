package com.example.role.controller;

import com.example.role.dto.PermissionDTOSJW;
import com.example.role.dto.RoleDTOSJW;
import com.example.role.service.RoleServiceSJW;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class RoleControllerSJW {
    private final RoleServiceSJW roleService;

    @GetMapping("/role")
    public List<RoleDTOSJW> getAllRoles() {
        return roleService.getAll();
    }
    @GetMapping("/role/active")
    public List<RoleDTOSJW> getActiveRoles() {
        return roleService.getActiveRoles();
    }
    @GetMapping("/role/{roleId}")
    public RoleDTOSJW getRole(@PathVariable Integer roleId) {
        return roleService.getRole(roleId);
    }

    @GetMapping("/permission")
    public List<PermissionDTOSJW> getAllPermission() {
        return roleService.getAllPermission();
    }

    @PostMapping("/role")
    public RoleDTOSJW registerRole(@RequestBody RoleDTOSJW role) {
        return roleService.registerRole(role);
    }

    @PutMapping("/role/{roleId}")
    public RoleDTOSJW modifyRole(@RequestBody RoleDTOSJW role,@PathVariable Integer roleId) {
        return roleService.modifyRole(role);
    }

    @PutMapping("/role/{id}/activation")
    public Boolean modifyRoleActivation(@RequestBody RoleDTOSJW role,@PathVariable Integer id) {
        return roleService.modifyRoleActivation(role,id);
    }

    @DeleteMapping("/role")
    public Boolean removeRole(Integer roleId) {
        return roleService.removeRole(roleId);
    }
}