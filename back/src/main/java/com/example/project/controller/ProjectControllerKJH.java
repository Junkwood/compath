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
    public ProjectSingleDtoKJH modifyProject(@PathVariable("projectId") Integer projectId,
                             @RequestBody ProjectModifyDtoKJH dto) {
        dto.setProjectId(projectId);
        Integer result = service.modifyProject(dto);

        return service.getSingleProject(projectId);
    }

    // 프로젝트 구성원 등록
    @PostMapping("/projects/registerMember")
    public List<ProjectMemberDtoKJH> registerProjectMember(@RequestBody ProjectMemberDtoKJH dto) {
        return service.registerMember(dto);
    }

    // 프로젝트 구성원 조회
    @GetMapping("/projects/getMembers/{id}")
    public List<ProjectMemberDtoKJH> getProjectMember(@PathVariable("id") int id) {
        return service.getAllProjectMem(id);
    }

    // 프로젝트 구성원 삭제
    @DeleteMapping("/projects/delMembers")
    public List<ProjectMemberDtoKJH> removeProjectMem(@RequestBody ProjectMemberDtoKJH dto) {

        return service.removeProjectMem(dto);
    }

}
