package com.example.role.mapper;

import com.example.role.dto.PermissionDTOSJW;
import com.example.role.dto.RoleDTOSJW;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapperSJW {
    List<RoleDTOSJW> getAll();
    List<PermissionDTOSJW> getAllPermission();
    Integer registerRole(RoleDTOSJW role);
    void deleteRolePermissionByRoleId(Integer roleId);
    Integer deleteRoleById(Integer roleId);
    void registerRolePermission(PermissionDTOSJW permission);
    Integer modifyRole(RoleDTOSJW role);
}