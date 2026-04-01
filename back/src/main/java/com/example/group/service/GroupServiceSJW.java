package com.example.group.service;

import com.example.group.dto.GroupDTOSJW;
import com.example.group.entity.GroupVOSJW;

import java.util.List;

public interface GroupServiceSJW {
    List<GroupVOSJW> getAll();
    List<GroupDTOSJW> getAllGroupForEmpRegister();
    GroupDTOSJW getById(Integer id);
    String registerGroup(GroupDTOSJW group);
    String checkDuplicatedName(String name);
    String modifyGroup(GroupDTOSJW group);
}