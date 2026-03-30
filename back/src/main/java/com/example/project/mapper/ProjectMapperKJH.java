package com.example.project.mapper;

import com.example.project.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectMapperKJH {

    //프로젝트 개별 상세조회
    public ProjectSingleDtoKJH getSingleProject(int projectId);

}

