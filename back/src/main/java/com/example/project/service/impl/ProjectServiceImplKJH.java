package com.example.project.service.impl;

import com.example.project.dto.*;
import com.example.project.mapper.ProjectMapperJDJ;
import com.example.project.mapper.ProjectMapperKJH;
import com.example.project.service.ProjectServiceJDJ;
import com.example.project.service.ProjectServiceKJH;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class ProjectServiceImplKJH implements ProjectServiceKJH {

    private final ProjectMapperKJH mapper;

    @Override
    public ProjectSingleDtoKJH getSingleProject(int projectId) {
        return mapper.getSingleProject(projectId);
    }
}
