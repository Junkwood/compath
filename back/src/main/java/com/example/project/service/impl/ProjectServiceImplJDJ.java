package com.example.project.service.impl;

import com.example.alarm.service.NotificationService;
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
    private final NotificationService notificationService;

    //프로젝트 전체목록 조회
    @Override
    public List<ProjectListDto> getAllProject(Integer userId) {
        return projectMapperJDJ.getAllProject(userId);
    }
    //프로젝트 생성
    @Override
    public void registerProject(ProjectCreateDtoJDJ dto) {
        dto.setPmUserId(dto.getCreatedBy());

        projectMapperJDJ.registerProject(dto);
        //기본 멤버 등록처리
        projectMapperJDJ.registerProjectMember(dto.getPmUserId(),dto.getProjectId(),17);
        projectMapperJDJ.registerProjectMember(dto.getPlUserId(),dto.getProjectId(),18);
        // 총괄PL한테 알림
        notificationService.sendToOne(
                dto.getPlUserId(),
                "R2", dto.getProjectId(),
                "프로젝트 배정", "총괄PL로 배정되었습니다.",
                dto.getCreatedBy()
        );

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

    //하위프로젝트 삭제 (상태값 업데이트 F1 에서 F2 로)
    @Override
    public void removeSubProjectStatus( ProjectSubCreateDtoJDJ dto) {
        int result = projectMapperJDJ.removeSubProjectStatus(dto);

        if (result == 0) {
            throw new RuntimeException("하위프로젝트 삭제(상태값 변경) 실패");
        }
    }


}
