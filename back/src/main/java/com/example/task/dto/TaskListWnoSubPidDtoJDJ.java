package com.example.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskListWnoSubPidDtoJDJ {
    private String title;
    private Integer taskId;
    private Integer projectId;
    private Integer taskStatusId;
    private Integer taskTypeId;
    private Integer milestoneId;
    private LocalDate startDate;
    private LocalDate estEndDate;
    private Integer progressRate;
    private String userName;
    private String typeName;
    private String priorityCode;
    private String codeName;
}
