package com.example.role.service;

import com.example.role.dto.PermissionDTOSJW;
import com.example.role.dto.RoleDTOKJH;
import com.example.role.dto.RoleDTOSJW;

import java.util.List;

public interface RoleServiceKJH {
    List<RoleDTOKJH> getNoticeRole(Integer id);
}