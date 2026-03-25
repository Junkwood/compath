package com.example.emp.service;

import com.example.emp.entity.EmpVOSJW;

import java.util.List;

public interface EmpServiceSJW {
    List<EmpVOSJW> findAll();
    EmpVOSJW findById(Integer id);
}