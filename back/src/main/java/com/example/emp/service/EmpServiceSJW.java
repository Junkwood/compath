package com.example.emp.service;

import com.example.emp.dto.AccountDTOSJW;
import com.example.emp.dto.EmpDTOSJW;
import com.example.emp.entity.EmpVOSJW;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EmpServiceSJW {
    List<EmpDTOSJW> getAll();
    EmpVOSJW getById(Integer id);
    String modifyStatusById(Integer id);
    Integer registerEmp(EmpVOSJW emp);
    EmpVOSJW login(EmpVOSJW emp);
    Boolean modifyEmpById(EmpVOSJW emp);
    Integer sendEmail(String email);
}