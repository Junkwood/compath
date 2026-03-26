package com.example.task.controller;

import com.example.milestone.dto.MilestoneDtoJJW;
import com.example.project.dto.ProjectDtoJJW;
import com.example.task.dto.*;
import com.example.task.service.TaskServiceJJW;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TaskControllerJJW {

    final TaskServiceJJW taskServiceJJW;

    //기본 업무 등록
    @PostMapping("/tasks")
    public TaskReqDtoJJW registerTasks(@RequestBody TaskReqDtoJJW dto){
     taskServiceJJW.insert(dto);
     return dto;
    }

    //프로젝트 조회
    @ResponseBody
    @GetMapping("/projectList")
    public List<ProjectDtoJJW> list(){
        return  taskServiceJJW.getProjectList();
    }

    //프로젝트 상세 조회
    @ResponseBody
    @GetMapping("/projectDetail/{projectId}")
    public ProjectDtoJJW projectDetail(@PathVariable("projectId") Integer projectId) {
        return taskServiceJJW.getProjectDetail(projectId);
    }
    //업무 상태 조회
    @ResponseBody
    @GetMapping("/taskStatus")
    public  List<TaskStatusDtoJJW> list1(){
        return  taskServiceJJW.getTaskStatus();
    }

    //업무 유형 조회
    @ResponseBody
    @GetMapping("/taskType")
    public List<TaskTypeDtoJJW> list2(){
        return  taskServiceJJW.getTaskType();
    }

    //담당자 조회
    @ResponseBody
    @GetMapping("/taskUser")
    public List<UserDtoJJW> list3(){
        return  taskServiceJJW.getUserList();
    }

    //마일스톤 조회
    @ResponseBody
    @GetMapping("/taskMileStone")
    public  List<MilestoneDtoJJW> list4(@RequestParam("projectId") Integer projectId){
        return taskServiceJJW.getMilestone(projectId);
    }


}
