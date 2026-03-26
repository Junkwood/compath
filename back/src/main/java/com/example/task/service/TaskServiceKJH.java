package com.example.task.service;

import com.example.task.dto.TaskListDTOKJH;

import java.util.List;

public interface TaskServiceKJH {

    // 업무 목록
    public List<TaskListDTOKJH> getAllTasks(String id, String pid);

    // 프로젝트 이름
    public TaskListDTOKJH getProjectName(Integer id);
}