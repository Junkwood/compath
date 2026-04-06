package com.example.project.controller;

import com.example.project.dto.*;
import com.example.project.service.ProjectServiceJDJ;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProjectControllerJDJ {

    final ProjectServiceJDJ projectServiceJDJ;

    //프로젝트 전체목록 조회e
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

    //하위프로젝트 생성 시 그 프로젝트의 role이 PL인 사람 불러오기
    @GetMapping("/ProjectRolePlList/{projectId}")
    public List<ProjectRolePlListDto> plRoleList(@PathVariable int projectId){
        return projectServiceJDJ.getPlRoleList(projectId);
    }

    //프로젝트 개별 대쉬보드-----------------------------------------------
    //프로젝트 개별 상세조회
    @GetMapping("/ProjectDetail/{projectId}")
    public ProjectSingleDtoJDJ pdlist(@PathVariable Integer projectId){
        return projectServiceJDJ.getSingleProject(projectId);
    }

    //하위프로젝트 목록 조회
    @GetMapping("/ProjectSubDetail/{parentProjectId}")
    public List<ProjectSubListDtoJDJ> subplist(@PathVariable Integer parentProjectId){
            return projectServiceJDJ.getSubpList(parentProjectId);
        }
      
    //하위프로젝트 생성
    @PostMapping("/ProjectSubRegister")
    public ProjectSubCreateDtoJDJ registerSubProject(@RequestBody ProjectSubCreateDtoJDJ dto){
        projectServiceJDJ.registerSubProject(dto);
        return dto;
    }

}


