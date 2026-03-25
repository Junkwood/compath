package com.example.task.controller;

import com.example.task.dto.TaskReqDtoJJW;
import com.example.task.service.TaskServiceJJW;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
