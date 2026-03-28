package com.example.task.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TaskEntityKJH {

    // task table
    private Integer taskId;
    private Integer parentTaskId;
    private String projectId;
    private Integer milestoneId;
    private Integer taskTypeId;
    private Integer taskStatusId;
    private String title;
    private String content;
    private Integer assigneeUserId;
    private String priorityCode;
    private Integer progressRate;
    private Date startDate;
    private Date dueDate;
    private Integer estimatedHours;
    private Integer actualHours;
    private String isDeleted;
    private Integer createdBy;
    private Date createdAt;
    private Date updatedAt;
    private Date estStartDate;
    private Date estEndDate;
    private Integer attachmentGroupId;
    private Integer editorUserId;

    // task_statuses
    private String statusName;

    // task_types
    private String typeName;

    // projects
    private String projectName;
    private Date endDate;

    // commonCode
    private String codeName;

    // users
    private String userName;
}