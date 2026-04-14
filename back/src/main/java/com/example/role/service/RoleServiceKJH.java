package com.example.role.service;

import com.example.role.dto.PermissionDTOSJW;
import com.example.role.dto.RoleDTOKJH;
import com.example.role.dto.RoleDTOSJW;

import java.util.List;

public interface RoleServiceKJH {
    List<RoleDTOKJH> getNoticeRole(Integer id);

    //    프로젝트별 권한 조회
    List<RoleDTOKJH> getProjectRole(RoleDTOKJH dto);
}