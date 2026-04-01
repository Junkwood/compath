package com.example.group.mapper;

import com.example.group.dto.GroupDTOSJW;
import com.example.group.entity.GroupVOSJW;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupMapperSJW {
    List<GroupVOSJW> getAll();
    GroupDTOSJW getById(Integer id);
    Integer registerGroup(GroupDTOSJW emp);
    List<GroupDTOSJW>getAllGroupForEmpRegister();
    Integer checkDuplicatedName(String name);
}