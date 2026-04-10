package com.example.project.service.impl;

import com.example.alarm.service.NotificationService;
import com.example.project.dto.*;
import com.example.project.mapper.ProjectMapperKJH;
import com.example.project.service.ProjectServiceKJH;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Exception.class)
public class ProjectServiceImplKJH implements ProjectServiceKJH {

    private final ProjectMapperKJH mapper;
    private final NotificationService notificationService;

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
    public List<ProjectMemberDtoKJH> registerMember(List<ProjectMemberDtoKJH> dtoList) {
        for(ProjectMemberDtoKJH dto : dtoList){
        // 프로젝트 구성원 테이블 추가
        mapper.registerMember(dto);
        // 구성원 테이블 기본키 값 조회
        int id = mapper.getProjectMember(dto.getUserId(),dto.getProjectId());
        // dto에 값 대입
        dto.setProjectMemberId(id);
        // 구성원 역할 테이블에 등록
        mapper.registerMemRole(dto);
        }
        // 알림 전송
        for (ProjectMemberDtoKJH dto : dtoList) {
            notificationService.sendToOne(
                    dto.getUserId(),
                    "R2",
                    dto.getProjectId(),
                    "프로젝트 참여",
                    "프로젝트에 참여되었습니다.",
                    0
            );
        }


        // 구서원 조회
        return mapper.getAllProjectMem(dtoList.getFirst().getProjectId());
    }

    @Override
    public List<ProjectMemberDtoKJH> getAllProjectMem(int id) {
        return mapper.getAllProjectMem(id);
    }

//    구성원 삭제
    @Override
    public List<ProjectMemberDtoKJH> removeProjectMem(ProjectMemberDtoKJH dto) {
        Integer id = dto.getProjectMemberRoleId();
        Integer pMid = dto.getProjectMemberId();
        Integer pid = dto.getProjectId();
        mapper.removeProjectMemRole(id);
        mapper.removeProjectMem(pMid);

        return mapper.getAllProjectMem(pid);
    }
}

