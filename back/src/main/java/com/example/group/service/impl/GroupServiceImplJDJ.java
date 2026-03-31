package com.example.group.service.impl;

import com.example.group.dto.GroupMemListDtoJDJ;
import com.example.group.mapper.GroupMapperJDJ;
import com.example.group.service.GroupServiceJDJ;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImplJDJ implements GroupServiceJDJ {

    private final GroupMapperJDJ groupMapperJDJ;

    @Override
    public List<GroupMemListDtoJDJ> getGroupMemList() {
        return groupMapperJDJ.getGroupMemList();
    }
}
