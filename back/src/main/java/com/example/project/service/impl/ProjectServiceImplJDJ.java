package com.example.project.service.impl;

import com.example.project.dto.ProjectListDTO;
import com.example.project.mapper.ProjectMapperJDJ;
import com.example.project.service.ProjectServiceJDJ;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class ProjectServiceImplJDJ implements ProjectServiceJDJ {

    private final ProjectMapperJDJ projectMapperJDJ;

    //프로젝트 전체목록 조회
    @Override
    public List<ProjectListDTO> getAllProject() {
        return projectMapperJDJ.getAllProject();
    }

    //프로젝트 생성

    @Override
    public int registerProject(ProjectListDTO dto) {
        return projectMapperJDJ.registerProject(dto);
    }
}
