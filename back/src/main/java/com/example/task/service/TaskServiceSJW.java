package com.example.task.service;

import com.example.task.dto.TaskListDashDtoJDJ;
import com.example.task.dto.TaskStatusDTOSJW;
import com.example.task.dto.TaskSummaryDtoJDJ;

import java.util.List;

public interface TaskServiceSJW {
        List<TaskStatusDTOSJW> getTaskStatuses();
        Boolean modifyTaskStatusActivation(TaskStatusDTOSJW taskStatus);
        Boolean modifyTaskStatusIsFinal(TaskStatusDTOSJW taskStatus);

        Boolean modifyTaskStatus(TaskStatusDTOSJW taskStatus);

        Boolean registerTaskStatus(TaskStatusDTOSJW taskStatus);
}
