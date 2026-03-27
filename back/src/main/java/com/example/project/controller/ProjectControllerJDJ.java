package com.example.project.controller;


import com.example.project.dto.ProjectCreateDtoJDJ;
import com.example.project.dto.ProjectSingleDtoJDJ;
import com.example.project.dto.ProjectListDto;
import com.example.project.dto.ProjectUserDtoJDJ;
import com.example.project.service.ProjectServiceJDJ;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProjectControllerJDJ {

    final ProjectServiceJDJ projectServiceJDJ;

    //프로젝트 전체목록 조회
    @ResponseBody
    @GetMapping("/ProjectList")
    public List<ProjectListDto> list() {
        return projectServiceJDJ.getAllProject();
    }

    //프로젝트 생성
    @PostMapping("/ProjectRegister")
    public ProjectCreateDtoJDJ registerProject(@RequestBody ProjectCreateDtoJDJ dto){
        projectServiceJDJ.registerProject(dto);
        return dto;
    }

    //프로젝트 생성시 총괄PL 리스트 불러오기
    @GetMapping("/ProjectPlList")
    public List<ProjectUserDtoJDJ> plist(){
        return projectServiceJDJ.getPlList();
    }

    //프로젝트 개별 상세조회
    @GetMapping("/ProjectDetail/{projectId}")
    public ProjectSingleDtoJDJ pdlist(@PathVariable Integer projectId){
        return projectServiceJDJ.getSingleProject(projectId);
    }
}
