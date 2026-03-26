package com.example.task.controller;

import com.example.task.dto.TaskListDashDtoJDJ;
import com.example.task.service.TaskServiceJDJ;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class TaskControllerJDJ {

    final TaskServiceJDJ taskServiceJDJ;

    @ResponseBody
    @GetMapping("/TaskListDash")
    public List<TaskListDashDtoJDJ> list(){
        return taskServiceJDJ.getTaskListDash();
    }
}
