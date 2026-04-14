package com.example.role.service.impl;

import com.example.role.dto.PermissionDTOSJW;
import com.example.role.dto.RoleDTOKJH;
import com.example.role.dto.RoleDTOSJW;
import com.example.role.mapper.RoleMapperKJH;
import com.example.role.mapper.RoleMapperSJW;
import com.example.role.service.RoleServiceKJH;
import com.example.role.service.RoleServiceSJW;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImplKJH implements RoleServiceKJH {
    private final RoleMapperKJH mapper;


    @Override
    public List<RoleDTOKJH> getNoticeRole(Integer id) {
        return mapper.getNoticeRole(id);
    }

    @Override
    public List<RoleDTOKJH> getProjectRole(RoleDTOKJH dto) {
        return mapper.getProjectRole(dto);
    }
}