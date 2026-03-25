package com.example.task.controller;

import com.example.project.dto.ProjectDTOJDJ;
import com.example.task.dto.ProjectDtoJJW;
import com.example.task.dto.TaskReqDtoJJW;
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


}
