package com.example.task.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TaskReqDtoJJW {
    //업무생성
    Date createdAt;
    Date updatedAt;
    Date estStartDate;
    Date estEndDate;
    Integer attachmentGroupId;
    Integer editorUserId;
    Integer taskId;
    Integer parentTaskId;
    @NotNull(message = "프로젝트는 필수입니다.")
    Integer projectId;
    Integer milestoneId;
    @NotNull(message = "업무유형은 필수입니다.")
    Integer taskTypeId;
    @NotNull(message = "업무상태는 필수입니다.")
    Integer taskStatusId;
    @NotNull(message = "업무명은 필수입니다.")
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
    @NotNull(message = "우선순위는 필수입니다.")
    String priorityCode;
    @NotNull(message = "담당자는 필수입니다.")
    String assigneeName;
}
