package com.example.emp.mapper;

import com.example.emp.dto.AccountDTOSJW;
import com.example.emp.dto.EmpDTOSJW;
import com.example.emp.entity.EmpVOSJW;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface EmpMapperSJW {
    List<EmpDTOSJW> getAll();
    List<EmpVOSJW> getAllForGroup();
    EmpVOSJW getById(Integer id);
    void modifyStatusById(Map<String,Object> id);
    Integer registerEmp(EmpVOSJW emp);
    void insertGroupMember(EmpVOSJW emp);
    Integer modifyEmpById(EmpVOSJW emp);
    void deactivateGroupMember(@Param("userId") Integer userId, @Param("groupId") Integer groupId);
    List<Integer> selectActiveGroupIds(Integer userId);
    void updateGroupMemberPrimary(@Param("userId") Integer userId, @Param("groupId") Integer groupId,@Param("isPrimary") String isPrimary);
    void insertEmailAuth(AccountDTOSJW account);
    Integer verifyAuthCode(Integer code,Integer emailId);
    void cleanUpOldCodes(Integer emailId);

    EmpVOSJW getByIdForMyPage(Integer id);
}