package com.example.emp.mapper;

import com.example.emp.entity.EmpVOSJW;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmpMapperSJW {
    List<EmpVOSJW> findAll();
    EmpVOSJW findById(Integer id);
}