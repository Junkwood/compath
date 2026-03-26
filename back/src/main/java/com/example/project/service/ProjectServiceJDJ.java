package com.example.project.service;

import com.example.project.dto.ProjectListDto;

import java.util.List;

public interface ProjectServiceJDJ {

    //프로젝트 전체목록 조회
    public List<ProjectListDto> getAllProject();

    //프로젝트 생성
    int registerProject(ProjectListDto dto);
}
