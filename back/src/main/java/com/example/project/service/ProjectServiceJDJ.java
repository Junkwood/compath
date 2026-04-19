package com.example.project.service;

import com.example.project.dto.*;

import java.util.List;

public interface ProjectServiceJDJ {

    //프로젝트 전체목록 조회
    public List<ProjectListDto> getAllProject(Integer userId);

    //프로젝트 생성
    void registerProject(ProjectCreateDtoJDJ dto);

    //프로젝트 생성시 총괄PL 리스트 불러오기
    public List<ProjectUserDtoJDJ> getPlList();

    //하위프로젝트 생성 시 그 프로젝트의 role이 PL인 사람 불러오기
    public List<ProjectRolePlListDto> getPlRoleList(int projectId);

    //프로젝트 단건조회
    public ProjectSingleDtoJDJ getSingleProject(int projectId);

    //하위프로젝트 목록 조회
    public List<ProjectSubListDtoJDJ> getSubpList(int parentProjectId);

    //하위프로젝트 생성
    void registerSubProject(ProjectSubCreateDtoJDJ dto);

    //하위프로젝트 수정
    //프로젝트테이블 수정
    void modifySubProject(ProjectSubCreateDtoJDJ dto);
    //수정용조회
    ProjectSubCreateDtoJDJ getSubProjectDetail(int projectId);

    //하위프로젝트 삭제 (상태값 업데이트 F1 에서 F2 로)
    void removeSubProjectStatus(ProjectSubCreateDtoJDJ dto);
}
