package com.example.task.controller;

import com.example.task.dto.TaskDetailDTOKJH;
import com.example.task.dto.TaskListDTOKJH;
import com.example.task.service.TaskServiceKJH;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TaskControllerKJH {

    private final TaskServiceKJH service;

    // 목록 조회
    @GetMapping("/api/tasks")
    public List<TaskListDTOKJH> getAllTasks(TaskListDTOKJH dto){
        return service.getAllTasks(dto);
    }

    // 필터링 조건 조회
    @GetMapping("/api/tasksFilters/{id}")
    public Map<String, Object> getAllFilterInfo(@PathVariable Integer id) {
        return service.getAllFiterInfo(id);
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