package com.example.task.controller;

import com.example.task.dto.TaskStatusDTOSJW;
import com.example.task.service.TaskServiceSJW;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class TaskControllerSJW {

    final TaskServiceSJW taskService;
    //업무상태 목록 불러오기
    @GetMapping("/taskStatus")
    public List<TaskStatusDTOSJW> getTaskStatuses(){
        return taskService.getTaskStatuses();
    }
    @PutMapping("/taskStatus/modify/isActive")
    public Boolean modifyTaskStatusActivation(@RequestBody TaskStatusDTOSJW taskStatus){
        log.error(taskStatus.toString());
        return taskService.modifyTaskStatusActivation(taskStatus);
    }
    @PutMapping("/taskStatus/modify/isFinal")
    public Boolean modifyTaskStatusIsFinal(@RequestBody TaskStatusDTOSJW taskStatus){
        log.error(taskStatus.toString());
        return taskService.modifyTaskStatusIsFinal(taskStatus);
    }
    @PutMapping("/taskStatus/modify")
    public Boolean modifyTaskStatus(@RequestBody TaskStatusDTOSJW taskStatus){
        return taskService.modifyTaskStatus(taskStatus);
    }
    @PostMapping("/taskStatus")
    public Boolean registerTaskStatus(@RequestBody TaskStatusDTOSJW taskStatus){
        return taskService.registerTaskStatus(taskStatus);
    }

}
