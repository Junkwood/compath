package com.example.group.mapper;

import com.example.group.dto.GroupMemListDtoJDJ;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupMapperJDJ {
    List<GroupMemListDtoJDJ> getGroupMemList(int projectId);
}
