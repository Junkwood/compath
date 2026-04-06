package com.example.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskListSubDashDtoJDJ {
    private Integer taskId;
    private String title;
    private Integer assigneeUserId;
    private String userName;
    private String priorityCode;
    private Integer taskStatusId;
    private Integer projectId;
}
