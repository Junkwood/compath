package com.example.task.controller;

import com.example.task.dto.TaskListDTOKJH;
import com.example.task.entity.TaskEntityKJH;
import com.example.task.service.TaskServiceKJH;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
}