package com.example.task.controller;

import com.example.task.dto.TaskStatusDTOSJW;
import com.example.task.dto.TaskTypeDTOSJW;
import com.example.task.service.TaskServiceSJW;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class TaskControllerSJW {

    final TaskServiceSJW taskService;
    //업무상태 목록 불러오기
    @GetMapping("/taskStatus")
    public List<TaskStatusDTOSJW> getTaskStatuses(){
        return taskService.getTaskStatuses();
    }
    //업무 상태 활성화 및 비활성화
    @PutMapping("/taskStatus/modify/isActive")
    public Boolean modifyTaskStatusActivation(@RequestBody TaskStatusDTOSJW taskStatus){
        log.error(taskStatus.toString());
        return taskService.modifyTaskStatusActivation(taskStatus);
    }
    //업무 상태의 종료 여부 활성화 및 비활성화
    @PutMapping("/taskStatus/modify/isFinal")
    public Boolean modifyTaskStatusIsFinal(@RequestBody TaskStatusDTOSJW taskStatus){
        log.error(taskStatus.toString());
        return taskService.modifyTaskStatusIsFinal(taskStatus);
    }
    //업무 상태 수정
    @PutMapping("/taskStatus/modify/{taskStatusId}")
    public Boolean modifyTaskStatus(@RequestBody TaskStatusDTOSJW taskStatus,@PathVariable Integer taskStatusId){
        return taskService.modifyTaskStatus(taskStatus);
    }
    //업무 상태 등록
    @PostMapping("/taskStatus")
    public Boolean registerTaskStatus(@RequestBody TaskStatusDTOSJW taskStatus){
        return taskService.registerTaskStatus(taskStatus);
    }
    //업무 상태 이름 중복 체크.
    @GetMapping("/taskStatus/checkDup/{name}")
    public Boolean checkDup(@PathVariable("name") String name){
        return taskService.checkDup(name);
    }
    //업무 유형 목록 불러오기
    @GetMapping("/taskType")
    public List<TaskTypeDTOSJW>  getTaskTypes(){
        return taskService.getTaskTypes();
    }
}
