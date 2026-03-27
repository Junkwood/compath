package com.example.emp.service.impl;

import com.example.emp.dto.EmpDTOSJW;
import com.example.emp.entity.EmpVOSJW;
import com.example.emp.mapper.EmpMapperSJW;
import com.example.emp.service.EmpServiceSJW;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmpServiceImplSJW implements EmpServiceSJW {
    private final EmpMapperSJW empMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);

    @Override
    public List<EmpDTOSJW> getAll() {
        return empMapper.getAll();
    }

    @Override
    public EmpVOSJW getById(Integer id) {
        return empMapper.getById(id);
    }

    @Override
    public String modifyStatusById(Integer id) {
        Map<String, Object> params = new HashMap<>();
        params.put("emp_id", id);
        empMapper.modifyStatusById(params);
        String result =(String) params.get("changed_status");
        if(result.equals("O1")) {
            return "Y";
        }else{
            return "N";
        }
    }

    @Override
    public Integer registerEmp(EmpVOSJW emp) {
        emp.setPassword(encoder.encode(emp.getPassword()));
        if(emp.getUserType().equals("ADMIN")) {
            emp.setUserType("M1");
        }else{
            emp.setUserType("M2");
        }
        emp.setIsActive("O1");
        empMapper.registerEmp(emp);
        empMapper.insertGroupMember(emp.getUserId(), emp.getGroupId());
        return emp.getUserId();
    }

    @Override
    public EmpVOSJW login(EmpVOSJW emp) {
        Integer id = emp.getUserId();
        String password = emp.getPassword();
        EmpVOSJW result = this.getById(id);

        if(encoder.matches(password, result.getPassword())) {
            result.setPassword(null);
            return result;
        }
        else  {
            return null;
        }
    }

}