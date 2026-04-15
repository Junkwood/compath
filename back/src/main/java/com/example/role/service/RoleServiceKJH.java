package com.example.role.service;

import com.example.role.dto.PermissionDTOSJW;
import com.example.role.dto.RoleDTOKJH;
import com.example.role.dto.RoleDTOSJW;

import java.util.List;
import java.util.Map;

public interface RoleServiceKJH {



    //    프로젝트별 권한 조회
    Map<String, Object> getProjectRole(RoleDTOKJH dto);
}