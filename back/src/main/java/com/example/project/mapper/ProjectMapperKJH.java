package com.example.project.mapper;

import com.example.project.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectMapperKJH {

    //프로젝트 개별 상세조회
    public ProjectSingleDtoKJH getSingleProject(int projectId);

    //프로젝트 정보 수정
    public int modifyProject(ProjectModifyDtoKJH dto);

    //프로젝트 구성원 추가
    public int registerMember(ProjectMemberDtoKJH dto);

    //프로젝트 구성원 조회
    public int getProjectMember(int userId, int projectId);

    //프로젝트 구성원 역할 테이블
    public int registerMemRole(ProjectMemberDtoKJH dto);

    //프로젝트 구성원 및 역할 조회
    public List<ProjectMemberDtoKJH> getAllProjectMem(int id);

    //구성원 역할 테이블 삭제
    public int removeProjectMemRole(Integer id);

    //구성원 테이블 삭제
    public int removeProjectMem(Integer id);
}

