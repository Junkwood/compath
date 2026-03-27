package com.example.emp.mapper;

import com.example.emp.dto.EmpDTOSJW;
import com.example.emp.entity.EmpVOSJW;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface EmpMapperSJW {
    List<EmpDTOSJW> getAll();
    EmpVOSJW getById(Integer id);
    void modifyStatusById(Map<String,Object> id);
    Integer registerEmp(EmpVOSJW emp);
    void insertGroupMember(@Param("userId") Integer userId, @Param("groupId") Integer groupId);
}