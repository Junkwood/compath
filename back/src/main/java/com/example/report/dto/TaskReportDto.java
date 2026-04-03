package com.example.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TaskReportDto {
    //업무보고서
    Date createdAt;
    Date updatedAt;
    Date estStartDate;
    Date estEndDate;
    Integer attachmentGroupId;
    Integer editorUserId;
    Integer taskId;
    Integer parentTaskId;
    Integer projectId;
    Integer milestoneId;
    Integer taskTypeId;
    Integer taskStatusId;
    String title;
    String content;
    Integer assigneeUserId;
    Integer progressRate;
    Date startDate;
    Date dueDate;
    Integer estimatedHours;
    String actualHours;
    String isDeleted;
    Integer createdBy;
    String priorityCode;
    String assigneeName;

    String userName;
    String projectName;
    String typeName;

}
