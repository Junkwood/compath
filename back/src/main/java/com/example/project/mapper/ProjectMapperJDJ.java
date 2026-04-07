package com.example.project.mapper;

import com.example.project.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectMapperJDJ {

    //프로젝트 전체목록 조회
    public List<ProjectListDto> getAllProject();

    //프로젝트 생성
    int registerProject(ProjectCreateDtoJDJ dto);

    //프로젝트 생성시 총괄PL 리스트 불러오기
    public List<ProjectUserDtoJDJ> getPlList();

    //하위프로젝트 생성 시 그 프로젝트의 role이 PL인 사람 불러오기
    public List<ProjectRolePlListDto> getPlRoleList(int projectId);

    //프로젝트 개별 상세조회
    public ProjectSingleDtoJDJ getSingleProject(int projectId);

    //하위프로젝트 목록 조회
    public List<ProjectSubListDtoJDJ> getSubpList(int parentProjectId);

    //하위프로젝트 생성
    int registerSubProject(ProjectSubCreateDtoJDJ dto);
    int registerMilestoneMapping(ProjectSubCreateDtoJDJ dto);

    //하위프로젝트 수정
    //프로젝트테이블 수정
    int modifySubProject(ProjectSubCreateDtoJDJ dto);
    //매핑테이블수정
    void modifyMilestoneMapping(ProjectSubCreateDtoJDJ dto);
    //수정용조회
    ProjectSubCreateDtoJDJ getSubProjectDetail(int projectId);

}

