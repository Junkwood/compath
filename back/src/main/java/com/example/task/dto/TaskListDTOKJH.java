package com.example.task.dto;

import com.example.task.entity.TaskEntityKJH;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private Date endDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul" )
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDate dueDate;

    private String projectName;
    private String typeName;
    private String statusName;
    private String codeName;
    private String userName;

    public static TaskListDTOKJH fromTaskEntity(TaskEntityKJH task) {
        return new TaskListDTOKJH(task.getTaskId(), task.getParentTaskId(), task.getMilestoneId(), task.getTaskTypeId(),
                                  task.getTaskStatusId(), task.getTitle(), task.getContent(), task.getAssigneeUserId(),
                                  task.getPriorityCode(), task.getProgressRate(), task.getStartDate(), task.getEndDate(), task.getDueDate(), task.getProjectName(),
                                  task.getTypeName(), task.getStatusName(), task.getCodeName(), task.getUserName());
    }
}