package com.example.role.service.impl;

import com.example.role.dto.PermissionDTOSJW;
import com.example.role.dto.RoleDTOSJW;
import com.example.role.entity.RoleVOSJW;
import com.example.role.mapper.RoleMapperSJW;
import com.example.role.service.RoleServiceSJW;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImplSJW implements RoleServiceSJW {
    private final RoleMapperSJW roleMapper;

    @Override
    public List<RoleDTOSJW> getAll() {
        return roleMapper.getAll();
    }

    @Override
    public List<PermissionDTOSJW> getAllPermission() {
        return roleMapper.getAllPermission();
    }
    @Transactional
    @Override
    public RoleDTOSJW registerRole(RoleDTOSJW role) {
        roleMapper.registerRole(role);
        return role;
    }
    @Transactional
    @Override
    public RoleDTOSJW modifyRole(RoleDTOSJW role) {
        roleMapper.deleteRolePermissionByRoleId(role.getRoleId());
        //수정하는 매퍼 실행
        return role;
    }
    @Transactional
    @Override
    public Boolean deleteRole(Integer roleId) {
        roleMapper.deleteRolePermissionByRoleId(roleId);
        Integer result = roleMapper.deleteRoleById(roleId);
        if(result>0){
            return true;
        }
        return false;
    }


}