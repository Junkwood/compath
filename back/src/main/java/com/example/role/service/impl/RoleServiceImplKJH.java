package com.example.role.service.impl;

import com.example.emp.entity.EmpVOKJH;
import com.example.emp.mapper.EmpMapperKJH;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImplKJH implements RoleServiceKJH {
    private final RoleMapperKJH mapper;
    private final EmpMapperKJH empMapper;

    @Override
    public Map<String, Object> getProjectRole(RoleDTOKJH dto) {

        Map<String, Object> result = new HashMap<>();

        List<RoleDTOKJH> list = mapper.getProjectRole(dto);
        result.put("projectRoleList", list);

        List<EmpVOKJH> empList = empMapper.getAdminList();
        result.put("empList", empList);

        return result;
    }
}