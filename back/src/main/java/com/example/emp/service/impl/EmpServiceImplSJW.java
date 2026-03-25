package com.example.emp.service.impl;

import com.example.emp.entity.EmpVOSJW;
import com.example.emp.mapper.EmpMapperSJW;
import com.example.emp.service.EmpServiceSJW;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpServiceImplSJW implements EmpServiceSJW {
    private final EmpMapperSJW empMapperSJW;

    @Override
    public List<EmpVOSJW> findAll() {
        return empMapperSJW.findAll();
    }

    @Override
    public EmpVOSJW findById(Integer id) {
        return empMapperSJW.findById(id);
    }
}