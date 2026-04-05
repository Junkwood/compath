package com.example.group.mapper;

import com.example.group.dto.GroupDTOKJH;
import com.example.group.dto.GroupDTOSJW;
import com.example.group.entity.GroupVOSJW;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupMapperKJH {
    // 직군 그룹별멤버
    List<GroupDTOKJH> getAllgroupMembers(Integer id, String name);

    // 프로젝트 그룹 멤버
    List<GroupDTOKJH> getProjectgroupMembers();
}