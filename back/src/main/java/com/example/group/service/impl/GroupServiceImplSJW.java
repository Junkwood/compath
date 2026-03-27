package com.example.group.service.impl;

import com.example.group.dto.GroupDTOSJW;
import com.example.group.entity.GroupVOSJW;
import com.example.group.mapper.GroupMapperSJW;
import com.example.group.service.GroupServiceSJW;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImplSJW implements GroupServiceSJW {
    private final GroupMapperSJW groupMapper;

    @Override
    public List<GroupVOSJW> getAll() {
        return groupMapper.getAll();
    }

    @Override
    public List<GroupDTOSJW> getAllGroupForEmpRegister() {
        return groupMapper.getAllGroupForEmpRegister();
    }

    @Override
    public GroupVOSJW getById(Integer id) {
        return groupMapper.getById(id);
    }


    @Override
    public String registerGroup(GroupVOSJW emp) {
        Integer result = groupMapper.registerGroup(emp);
        if(result <= 0) {
            return "N";
        }
        return "Y";
    }


}