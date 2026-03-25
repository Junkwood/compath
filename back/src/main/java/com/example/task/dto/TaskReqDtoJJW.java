package com.example.task.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TaskReqDtoJJW {
    Date createdAt;
    Date updatedAt;
    Date estStartDate;
    Date estEndDate;
    Integer attachmentGroupId;
    Integer editorUserId;
    Integer taskId;
    Integer parentTaskId;
    String projectId;
    Integer milestoneId;
    Integer taskTypeId;
    Integer taskStatusId;
    String title;
    String content;
    Integer assigneeUserId;
    String priorityCode;
    String progressRate;
    Date startDate;
    Date dueDate;
    String estimatedHours;
    String actualHours;
    String isDeleted;
    Integer createdBy;
}
