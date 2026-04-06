package com.example.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusDTOSJW {
    Integer taskStatusId;
    String statusName;
    String description;
    String isActive;
    String isFinal;
    Date createdAt;
    Date updatedAt;
    Integer editorUserId;
    Integer createdBy;
    Integer userId;
}
