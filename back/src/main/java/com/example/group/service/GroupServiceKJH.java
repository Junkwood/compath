package com.example.group.service;

import com.example.group.dto.GroupDTOKJH;
import com.example.group.dto.GroupDTOSJW;
import com.example.group.entity.GroupVOSJW;

import java.util.List;

public interface GroupServiceKJH {
    List<GroupDTOKJH> getAllgroupMembers(Integer id);
}