//업무 등록
package com.example.task.service;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskVOJJW {
    Integer projectId;
    Integer parentTaskId;
    Integer milestoneId;

    Integer taskTypeId;
    Integer taskStatusId;

    @NotEmpty(message = "업무명은 필수 입니다.")
    String title;
    String content;

    Integer assigneeUserId;
    String priorityCode;

    Date estStartDate;
    Date estEndDate;

    String estimatedHours;
    Integer progressRate;
}
