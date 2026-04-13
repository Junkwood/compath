package com.example.emp.service;

import com.example.emp.dto.AccountDTOSJW;
import com.example.emp.dto.EmpDTOSJW;
import com.example.emp.entity.EmpVOSJW;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EmpServiceSJW {
    List<EmpDTOSJW> getAll();

    List<EmpVOSJW> getAllForGroup();

    EmpVOSJW getById(Integer id);

    String modifyStatusById(Integer id);

    Integer registerEmp(EmpVOSJW emp);

    EmpVOSJW login(EmpVOSJW emp);

    Boolean modifyEmpById(EmpVOSJW emp, Integer id);

    Integer sendEmail(String email, Integer userId);

    Boolean verifyCode(Integer code, Integer emailId);

    EmpVOSJW getByIdForMyPage(Integer id);

    Boolean modifyPasswordById(EmpDTOSJW emp, Integer id);

    EmpDTOSJW getAdminSummary();
}