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
        Integer result = roleMapper.registerRole(role);
        if (result > 0) {
            Integer roleId = role.getRoleId();
            List<Integer> permissionIds = role.getPermissionIds();
            if (permissionIds != null) {
                permissionIds.forEach(permissionId -> {
                    PermissionDTOSJW permission = new PermissionDTOSJW();
                    permission.setRoleId(roleId);
                    permission.setPermissionId(permissionId);
                    roleMapper.registerRolePermission(permission);
                });
            }

        }
        return role;
    }

    @Transactional
    @Override
    public RoleDTOSJW modifyRole(RoleDTOSJW role) {
        // 해당 role_id의 기존 role_permission 다 삭제
        roleMapper.deleteRolePermissionByRoleId(role.getRoleId());
        //roles수정하는 매퍼 실행
        Integer result = roleMapper.modifyRole(role);
        //role_permission 재등록
        if (result > 0) {
            Integer roleId = role.getRoleId();
            List<Integer> permissionIds = role.getPermissionIds();
            if (permissionIds != null) {
                permissionIds.forEach(permissionId -> {
                    PermissionDTOSJW permission = new PermissionDTOSJW();
                    permission.setRoleId(roleId);
                    permission.setPermissionId(permissionId);
                    roleMapper.registerRolePermission(permission);
                });
            }
        }
        return role;
    }

    @Transactional
    @Override
    public Boolean deleteRole(Integer roleId) {
        roleMapper.deleteRolePermissionByRoleId(roleId);
        Integer result = roleMapper.deleteRoleById(roleId);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean modifyRoleActivation(RoleDTOSJW role) {
        RoleDTOSJW roleDTO = new RoleDTOSJW();
        roleDTO.setRoleId(role.getRoleId());
        log.error(role.toString());
        if (role.getIsActive().equals("Y")) {
            roleDTO.setIsActive("O1");
        } else {
            roleDTO.setIsActive("O2");
        }
        log.error(roleDTO.toString());
        Integer result = roleMapper.modifyRole(roleDTO);
        if (result > 0) {
            return true;
        }
        return false;
    }


}