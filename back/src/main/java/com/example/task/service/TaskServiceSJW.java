package com.example.task.service;

import com.example.task.dto.TaskStatusDTOSJW;

import java.util.List;

public interface TaskServiceSJW {
        List<TaskStatusDTOSJW> getTaskStatuses();
        Boolean modifyTaskStatusActivation(TaskStatusDTOSJW taskStatus);
        Boolean modifyTaskStatusIsFinal(TaskStatusDTOSJW taskStatus);

        Boolean modifyTaskStatus(TaskStatusDTOSJW taskStatus);

        Boolean registerTaskStatus(TaskStatusDTOSJW taskStatus);
}
