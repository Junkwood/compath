package com.example.project.mapper;

import com.example.project.dto.ProjectDTOJDJ;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectMapperJDJ {

    //프로젝트 전체목록 조회
    public List<ProjectDTOJDJ> getAllProject();

    //프로젝트 생성
    int registerProject(ProjectDTOJDJ dto);

}

