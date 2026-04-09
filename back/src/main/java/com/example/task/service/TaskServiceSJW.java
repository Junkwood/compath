package com.example.task.service;

import com.example.task.dto.TaskStatusDTOSJW;
import com.example.task.dto.TaskTypeDTOSJW;

import java.util.List;

public interface TaskServiceSJW {
    List<TaskStatusDTOSJW> getTaskStatuses();

    Boolean modifyTaskStatusActivation(TaskStatusDTOSJW taskStatus);

    Boolean modifyTaskStatusIsFinal(TaskStatusDTOSJW taskStatus);

    Boolean modifyTaskStatus(TaskStatusDTOSJW taskStatus);

    Boolean registerTaskStatus(TaskStatusDTOSJW taskStatus);

    Boolean checkDup(String name);

    List<TaskTypeDTOSJW> getTaskTypes();

    List<TaskStatusDTOSJW> getActiveTaskStatuses();

    List<TaskTypeDTOSJW> getActiveTaskTypes();

    Boolean modifyTaskTypeActivation(TaskTypeDTOSJW taskType);

    Boolean checkDupType(String name);

    Boolean registerTaskType(TaskTypeDTOSJW taskType);

    Boolean modifyTaskType(Integer taskTypeId, TaskTypeDTOSJW taskType);
}
