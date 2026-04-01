package com.example.role.service;

import com.example.role.dto.RoleDTOSJW;
import com.example.role.entity.RoleVOSJW;

import java.util.List;

public interface RoleServiceSJW {
    List<RoleVOSJW> getAll();
}