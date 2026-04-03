package com.example.role.mapper;

import com.example.role.dto.PermissionDTOSJW;
import com.example.role.dto.RoleDTOSJW;
import com.example.role.entity.RoleVOSJW;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapperSJW {
    List<RoleDTOSJW> getAll();
    List<PermissionDTOSJW> getAllPermission();
    void registerRole(RoleDTOSJW role);
    void deleteRolePermissionByRoleId(Integer roleId);
    Integer deleteRoleById(Integer roleId);
}