package com.example.emp.service;

import com.example.emp.dto.EmpDTOSJW;
import com.example.emp.entity.EmpVOKJH;
import com.example.emp.entity.EmpVOSJW;

import java.util.List;

public interface EmpServiceKJH {
    EmpVOKJH getById(Integer id);
}