package com.example.role.mapper;

import com.example.role.dto.PermissionDTOSJW;
import com.example.role.dto.RoleDTOKJH;
import com.example.role.dto.RoleDTOSJW;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapperKJH {
//    <!--    공지사항/문서 생성시 카테고리 -->
    List<RoleDTOKJH> getNoticeRole(Integer id);

//    프로젝트별 권한 조회
    List<RoleDTOKJH> getProjectRole(RoleDTOKJH dto);

}