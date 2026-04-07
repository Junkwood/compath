package com.example.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskTypeDTOSJW {
    Integer taskTypeId;
    String typeName;
    String description;
    String isActive;
    Date createdAt;
    Date updatedAt;
    Integer editorUserId;
    Integer createdBy;
    Integer userId;
    Integer startStatus;
    List<TaskStatusDTOSJW> taskStatuses;
}
