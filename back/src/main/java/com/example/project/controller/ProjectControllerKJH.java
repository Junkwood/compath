package com.example.project.controller;


import com.example.project.dto.*;
import com.example.project.service.ProjectServiceJDJ;
import com.example.project.service.ProjectServiceKJH;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProjectControllerKJH {

    private final ProjectServiceKJH service;

    //프로젝트 개별 상세조회
    @GetMapping("/ProjectInfo/{projectId}")
    public ProjectSingleDtoKJH projectInfo(@PathVariable Integer projectId){
        return service.getSingleProject(projectId);
    }

    //프로젝트 수정
    @PutMapping("/projects/info/{projectId}")
    public int modifyProject(@PathVariable("projectId") Integer projectId,
                             @RequestBody ProjectModifyDtoKJH dto) {
        dto.setProjectId(projectId);
        return service.modifyProject(dto);
    }
}
