package com.example.role.service;

import com.example.role.dto.PermissionDTOSJW;
import com.example.role.dto.RoleDTOSJW;

import java.util.List;

public interface RoleServiceSJW {
    List<RoleDTOSJW> getAll();

    List<PermissionDTOSJW> getAllPermission();

    RoleDTOSJW registerRole(RoleDTOSJW role);

    RoleDTOSJW modifyRole(RoleDTOSJW role);

    Boolean deleteRole(Integer roleId);
}