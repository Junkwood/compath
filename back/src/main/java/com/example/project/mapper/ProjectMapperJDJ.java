package com.example.project.mapper;

import com.example.project.dto.ProjectCreateDtoJDJ;
import com.example.project.dto.ProjectSingleDtoJDJ;
import com.example.project.dto.ProjectListDto;
import com.example.project.dto.ProjectUserDtoJDJ;
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

    //프로젝트 개별 상세조회
    public ProjectSingleDtoJDJ getSingleProject(int projectId);

}

