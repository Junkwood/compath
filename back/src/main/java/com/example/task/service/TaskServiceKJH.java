package com.example.task.service;

import com.example.task.dto.TaskListDTOKJH;

import java.util.List;

public interface TaskServiceKJH {

    public List<TaskListDTOKJH> getAllTasks(String id, String pid);
}