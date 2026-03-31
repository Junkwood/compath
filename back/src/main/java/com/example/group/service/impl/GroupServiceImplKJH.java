package com.example.group.service.impl;

import com.example.group.dto.GroupDTOKJH;
import com.example.group.dto.GroupDTOSJW;
import com.example.group.entity.GroupVOSJW;
import com.example.group.mapper.GroupMapperKJH;
import com.example.group.mapper.GroupMapperSJW;
import com.example.group.service.GroupServiceKJH;
import com.example.group.service.GroupServiceSJW;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImplKJH implements GroupServiceKJH {

    private final GroupMapperKJH mapper;

    @Override
    public List<GroupDTOKJH> getAllgroupMembers(Integer id) {
        return mapper.getAllgroupMembers(id);
    }
}