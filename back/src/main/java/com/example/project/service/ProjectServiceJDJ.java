package com.example.project.service;

import com.example.project.dto.*;

import java.util.List;

public interface ProjectServiceJDJ {

    //프로젝트 전체목록 조회
    public List<ProjectListDto> getAllProject();

    //프로젝트 생성
    void registerProject(ProjectCreateDtoJDJ dto);

    //프로젝트 생성시 총괄PL 리스트 불러오기
    public List<ProjectUserDtoJDJ> getPlList();

    //프로젝트 단건조회
    public ProjectSingleDtoJDJ getSingleProject(int projectId);

    //하위프로젝트 목록 조회
    public List<ProjectSubListDtoJDJ> getSubpList(int parentProjectId);

    //하위프로젝트 생성
    void registerSubProject(ProjectSubCreateDtoJDJ dto);
}
