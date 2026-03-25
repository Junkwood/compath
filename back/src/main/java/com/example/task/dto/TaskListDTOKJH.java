package com.example.task.dto;

import com.example.task.entity.TaskEntityKJH;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskListDTOKJH {
    private Integer taskId;
    private Integer parentTaskId;
    private Integer milestoneId;
    private Integer taskTypeId;
    private Integer taskStatusId;
    private String title;
    private String content;
    private Integer assigneeUserId;
    private String priorityCode;
    private Integer progressRate;
    private Date startDate;
    private String projectName;
    private String typeName;
    private String statusName;

    public static TaskListDTOKJH fromTaskEntity(TaskEntityKJH task) {
        return new TaskListDTOKJH(task.getTaskId(), task.getParentTaskId(), task.getMilestoneId(), task.getTaskTypeId(),
                                  task.getTaskStatusId(), task.getTitle(), task.getContent(), task.getAssigneeUserId(),
                                  task.getPriorityCode(), task.getProgressRate(), task.getStartDate(), task.getProjectName(),
                                  task.getTypeName(), task.getStatusName());
    }
}