package com.example.project.service;

import com.example.project.dto.*;

import java.util.List;

public interface ProjectServiceKJH {
    //프로젝트 단건조회
    public ProjectSingleDtoKJH getSingleProject(int projectId);

    //프로젝트 수정
    public int modifyProject(ProjectModifyDtoKJH dto);

    //프로젝트 구성원 추가
    public List<ProjectMemberDtoKJH> registerMember(ProjectMemberDtoKJH dto);

    //프로젝트 구성원 및 역할 조회
    public List<ProjectMemberDtoKJH> getAllProjectMem(int id);
}
