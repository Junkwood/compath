package com.example.emp.mapper;

import com.example.emp.dto.AccountDTOSJW;
import com.example.emp.dto.EmpDTOSJW;
import com.example.emp.entity.EmpVOKJH;
import com.example.emp.entity.EmpVOSJW;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface EmpMapperKJH {
    EmpVOKJH getById(Integer id);

}