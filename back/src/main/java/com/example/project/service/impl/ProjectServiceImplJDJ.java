package com.example.project.service.impl;

import com.example.project.dto.*;
import com.example.project.mapper.ProjectMapperJDJ;
import com.example.project.service.ProjectServiceJDJ;
import com.example.task.dto.TaskSummaryDtoJDJ;
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
    public List<ProjectListDto> getAllProject() {
        return projectMapperJDJ.getAllProject();
    }

    //프로젝트 생성
    @Override
    public void registerProject(ProjectCreateDtoJDJ dto) {
        projectMapperJDJ.registerProject(dto);
    }

    //프로젝트 생성시 총괄PL 리스트 불러오기
    @Override
    public List<ProjectUserDtoJDJ> getPlList() {
        return projectMapperJDJ.getPlList();
    }

    //하위프로젝트 생성 시 그 프로젝트의 role이 PL인 사람 불러오기
    @Override
    public List<ProjectRolePlListDto> getPlRoleList(int projectId) {
        return projectMapperJDJ.getPlRoleList(projectId);
    }

    //프로젝트 개별 대쉬보드-----------------------------------------------
    //프로젝트 단건조회
    @Override
    public ProjectSingleDtoJDJ getSingleProject(int projectId) {
        return projectMapperJDJ.getSingleProject(projectId);
    }

    //하위프로젝트 목록 조회
    @Override
    public List<ProjectSubListDtoJDJ> getSubpList(int parentProjectId) {
        return projectMapperJDJ.getSubpList(parentProjectId);
    }

    //하위프로젝트 생성
    @Override
    public void registerSubProject(ProjectSubCreateDtoJDJ dto) {
        projectMapperJDJ.registerSubProject(dto);

        if (dto.getMilestoneId() != null) {
            projectMapperJDJ.registerMilestoneMapping(dto);
        }
    }

    //하위프로젝트 수정용 단건조회
    @Override
    public ProjectSubCreateDtoJDJ getSubProjectDetail(int projectId){
        return projectMapperJDJ.getSubProjectDetail(projectId);
    }

    //하위프로젝트 수정
    @Override
    public void modifySubProject(ProjectSubCreateDtoJDJ dto) {
        projectMapperJDJ.modifySubProject(dto);

        if (dto.getMilestoneMappingId() != null) {
            projectMapperJDJ.modifyMilestoneMapping(dto);
        } else if (dto.getMilestoneId() != null) {
            projectMapperJDJ.registerMilestoneMapping(dto);
        }
    }



}
