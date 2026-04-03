package com.example.task.controller;

import com.example.task.dto.TaskDetailDTOKJH;
import com.example.task.dto.TaskListDTOKJH;
import com.example.task.entity.TaskEntityKJH;
import com.example.task.service.TaskServiceKJH;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaskControllerKJH {

    private final TaskServiceKJH service;

    // 목록 조회
    @GetMapping("/api/tasks/{id}")
    public List<TaskListDTOKJH> getAllTasks(@PathVariable String id){
        return service.getAllTasks(id, id);
    }

    // 프로젝트 이름 조회
    @GetMapping("/api/tasks/projectname/{id}")
    public TaskListDTOKJH getProjectName(@PathVariable Integer id){
        return service.getProjectName(id);
    }

    // 업무 단건 조회
    @GetMapping("/api/tasks/detail/{id}")
    public TaskDetailDTOKJH getTaskById(@PathVariable Integer id){
        return service.getTaskById(id);
    }

    // 소요시간 등록
    @PostMapping("/api/tasks/timelog")
    public List<TaskDetailDTOKJH> registerTimeEntries(@RequestBody TaskDetailDTOKJH dto) {
        return service.registerTimeEntries(dto);
    }

    //소요시간 단건 조회
    @GetMapping("/api/tasks/timelog/{id}")
    public List<TaskDetailDTOKJH> getTimeEntriesById(@PathVariable Integer id) {
        return service.getTimeEntriesById(id);
    }

    // 활동내역 목록 조회
    @GetMapping("/api/tasks/activityLog/{id}")
    public List<TaskDetailDTOKJH> getTaskActivityLogs(@PathVariable Integer id) {
        return service.getTimeLog(id);
    }
}