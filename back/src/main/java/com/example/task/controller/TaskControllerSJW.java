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
    @GetMapping("/task/status")
    public List<TaskStatusDTOSJW> getTaskStatuses() {
        return taskService.getTaskStatuses();
    }

    // 활성화된 업무상태 목록 불러오기
    @GetMapping("/task/status/active")
    public List<TaskStatusDTOSJW> getActiveTaskStatuses() {
        return taskService.getActiveTaskStatuses();
    }

    //업무 상태 활성화 및 비활성화
    @PutMapping("/task/status/modify/isActive")
    public Boolean modifyTaskStatusActivation(@RequestBody TaskStatusDTOSJW taskStatus) {
        log.error(taskStatus.toString());
        return taskService.modifyTaskStatusActivation(taskStatus);
    }

    //업무 상태의 종료 여부 활성화 및 비활성화
    @PutMapping("/task/status/modify/isFinal")
    public Boolean modifyTaskStatusIsFinal(@RequestBody TaskStatusDTOSJW taskStatus) {
        log.error(taskStatus.toString());
        return taskService.modifyTaskStatusIsFinal(taskStatus);
    }

    //업무 상태 수정
    @PutMapping("/task/status/modify/{taskStatusId}")
    public Boolean modifyTaskStatus(@RequestBody TaskStatusDTOSJW taskStatus, @PathVariable Integer taskStatusId) {
        return taskService.modifyTaskStatus(taskStatus);
    }

    //업무 상태 등록
    @PostMapping("/task/status")
    public Boolean registerTaskStatus(@RequestBody TaskStatusDTOSJW taskStatus) {
        return taskService.registerTaskStatus(taskStatus);
    }

    //업무 상태 이름 중복 체크.
    @GetMapping("/task/status/checkDup/{name}")
    public Boolean checkDup(@PathVariable("name") String name) {
        return taskService.checkDup(name);
    }

    //업무 유형 목록 불러오기
    @GetMapping("/task/type")
    public List<TaskTypeDTOSJW> getTaskTypes() {
        return taskService.getTaskTypes();
    }
    //활성화된 업무 유형 목록 불러오기
    @GetMapping("/task/type/active")
    public List<TaskTypeDTOSJW> getActiveTaskTypes() {
        return taskService.getActiveTaskTypes();
    }
    //업무 유형 활성화 및 비활성화
    @PutMapping("/task/type/modify/isActive")
    public Boolean modifyTaskTypeActivation(@RequestBody TaskTypeDTOSJW taskType) {
        log.error(taskType.toString());
        return taskService.modifyTaskTypeActivation(taskType);
    }
    //업무 유형 이름 중복 체크.
    @GetMapping("/task/type/checkDup/{name}")
    public Boolean checkDupType(@PathVariable("name") String name) {
        return taskService.checkDupType(name);
    }
    //업무 유형 등록
    @PostMapping("/task/type")
    public Boolean registerTaskType(@RequestBody TaskTypeDTOSJW taskType) {
        return taskService.registerTaskType(taskType);
    }
    //업무 유형 등록
    @PutMapping("/task/type/modify/{taskTypeId}")
    public Boolean modifyTaskType(@RequestBody TaskTypeDTOSJW taskType,@PathVariable Integer taskTypeId) {
        return taskService.modifyTaskType(taskTypeId,taskType);
    }
}
