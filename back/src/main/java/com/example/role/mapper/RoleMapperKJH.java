package com.example.role.mapper;

import com.example.role.dto.PermissionDTOSJW;
import com.example.role.dto.RoleDTOKJH;
import com.example.role.dto.RoleDTOSJW;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapperKJH {

//    프로젝트별 권한 조회
    List<RoleDTOKJH> getProjectRole(RoleDTOKJH dto);

}