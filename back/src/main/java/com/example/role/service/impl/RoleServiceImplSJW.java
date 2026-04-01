package com.example.role.service.impl;

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
    public List<RoleVOSJW> getAll() {
        return roleMapper.getAll();
    }


}