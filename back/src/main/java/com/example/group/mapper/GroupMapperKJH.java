package com.example.group.mapper;

import com.example.group.dto.GroupDTOKJH;
import com.example.group.dto.GroupDTOSJW;
import com.example.group.entity.GroupVOSJW;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupMapperKJH {
    List<GroupDTOKJH> getAllgroupMembers(Integer id);
}