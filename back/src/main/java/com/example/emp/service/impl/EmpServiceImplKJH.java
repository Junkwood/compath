package com.example.emp.service.impl;

import com.example.emp.dto.AccountDTOSJW;
import com.example.emp.dto.EmpDTOSJW;
import com.example.emp.entity.EmpVOKJH;
import com.example.emp.entity.EmpVOSJW;
import com.example.emp.mapper.EmpMapperKJH;
import com.example.emp.mapper.EmpMapperSJW;
import com.example.emp.service.EmpServiceKJH;
import com.example.emp.service.EmpServiceSJW;
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
public class EmpServiceImplKJH implements EmpServiceKJH {

    private final EmpMapperKJH empMapper;

    @Override
    public EmpVOKJH getById(Integer id) {
        return empMapper.getById(id);
    }



}