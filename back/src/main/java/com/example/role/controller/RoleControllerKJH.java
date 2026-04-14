package com.example.role.controller;

import com.example.role.dto.PermissionDTOSJW;
import com.example.role.dto.RoleDTOKJH;
import com.example.role.dto.RoleDTOSJW;
import com.example.role.service.RoleServiceKJH;
import com.example.role.service.RoleServiceSJW;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RoleControllerKJH {
    private final RoleServiceKJH service;

//    문서/공지사항 생성시 카테고리
    @GetMapping("/notice/roleList/{id}")
    public List<RoleDTOKJH> getAllRoles(@PathVariable Integer id) {

        return service.getNoticeRole(id);
    }

//    프로젝트 내 권한 조회
    @GetMapping("/role/roleList")
    public List<RoleDTOKJH> getProjectRole( RoleDTOKJH dto) {
        return service.getProjectRole(dto);
    }
}