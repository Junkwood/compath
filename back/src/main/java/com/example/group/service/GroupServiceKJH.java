package com.example.group.service;

import com.example.group.dto.GroupDTOKJH;
import com.example.group.dto.GroupDTOSJW;
import com.example.group.entity.GroupVOSJW;

import java.util.List;

public interface GroupServiceKJH {
    // 직군그룹
    List<GroupDTOKJH> getAllgroupMembers(Integer id, String name);

    // 프로젝트 그룹 멤버들
    List<GroupDTOKJH> getProjectgroupMembers();
}