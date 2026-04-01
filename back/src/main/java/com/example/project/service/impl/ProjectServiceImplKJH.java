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

//    프로젝트 단건 조회
    @Override
    public ProjectSingleDtoKJH getSingleProject(int projectId) {
        return mapper.getSingleProject(projectId);
    }

//    프로젝트 설정 수정
    @Override
    public int modifyProject(ProjectModifyDtoKJH dto) {
        return mapper.modifyProject(dto);
    }

//    프로젝트 구성원 등록
    @Override
    @Transactional
    public List<ProjectMemberDtoKJH> registerMember(ProjectMemberDtoKJH dto) {
        // 프로젝트 구성원 테이블 추가
        mapper.registerMember(dto);
        // 구성원 테이블 기본키 값 조회
        int id = mapper.getProjectMember(dto.getUserId(),dto.getProjectId());
        // dto에 값 대입
        dto.setProjectMemberId(id);
        // 구성원 역할 테이블에 등록
        mapper.registerMemRole(dto);

        return mapper.getAllProjectMem(dto.getProjectId());
    }

    @Override
    public List<ProjectMemberDtoKJH> getAllProjectMem(int id) {
        return mapper.getAllProjectMem(id);
    }


}
