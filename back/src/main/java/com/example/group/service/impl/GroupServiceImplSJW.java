package com.example.group.service.impl;

import com.example.emp.entity.EmpVOSJW;
import com.example.emp.mapper.EmpMapperSJW;
import com.example.group.dto.GroupDTOSJW;
import com.example.group.entity.GroupVOSJW;
import com.example.group.mapper.GroupMapperSJW;
import com.example.group.service.GroupServiceSJW;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImplSJW implements GroupServiceSJW {
    private final GroupMapperSJW groupMapper;
    private final EmpMapperSJW empMapperSJW;

    @Override
    public List<GroupVOSJW> getAll() {
        return groupMapper.getAll();
    }

    @Override
    public List<GroupDTOSJW> getAllGroupForEmpRegister() {
        return groupMapper.getAllGroupForEmpRegister();
    }

    @Override
    public GroupDTOSJW getById(Integer id) {
        return groupMapper.getById(id);
    }


    @Override
    public String checkDuplicatedName(String name) {
        Integer result = groupMapper.checkDuplicatedName(name);
        if(result <= 0) {
            return "Y";
        }
        return "N";
    }

    @Override
    @Transactional
    public String registerGroup(GroupDTOSJW group) {
        Integer result = groupMapper.registerGroup(group);
        if(result > 0) {
            group.getMembers().forEach(item -> {
                EmpVOSJW emp = new EmpVOSJW();
                emp.setUserId(item.getUserId());
                emp.setGroupId(group.getGroupId());
                emp.setIsPrimary("N");
                emp.setRoleId(item.getRoleId());
                empMapperSJW.insertGroupMember(emp);
            });
            return "Y";
        }
        return "N";
    }

    @Override
    public String modifyGroup(GroupDTOSJW group) {
        return "";
    }


}