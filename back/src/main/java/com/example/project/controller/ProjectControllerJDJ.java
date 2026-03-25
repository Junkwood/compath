package com.example.project.controller;


import com.example.project.dto.ProjectDTOJDJ;
import com.example.project.service.ProjectServiceJDJ;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProjectControllerJDJ {

    final ProjectServiceJDJ projectServiceJDJ;

    //프로젝트 전체목록 조회
    @ResponseBody
    @GetMapping("/api/ProjectList")
    public List<ProjectDTOJDJ> list() {
        return projectServiceJDJ.getAllProject();
    }

    //프로젝트 생성
    @PostMapping("/api/ProjectRegister")
    public ProjectDTOJDJ registerProject(@RequestBody ProjectDTOJDJ dto){
        projectServiceJDJ.registerProject(dto);
        return dto;
    }
}
