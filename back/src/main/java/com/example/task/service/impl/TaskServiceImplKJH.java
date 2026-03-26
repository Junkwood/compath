package com.example.task.service.impl;

import com.example.task.dto.TaskListDTOKJH;
import com.example.task.entity.TaskEntityKJH;
import com.example.task.mapper.TaskMapperKJH;
import com.example.task.service.TaskServiceKJH;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImplKJH implements TaskServiceKJH {

    private final TaskMapperKJH mapper;

    @Override
    public List<TaskListDTOKJH> getAllTasks(String id, String pid) {
        List<TaskEntityKJH> list = mapper.getAllTasks(id, pid);

        return list.stream().map(TaskListDTOKJH::fromTaskEntity).collect(Collectors.toList());
    }

    @Override
    public TaskListDTOKJH getProjectName(Integer id) {

        return mapper.getProjectName(id);
    }
}