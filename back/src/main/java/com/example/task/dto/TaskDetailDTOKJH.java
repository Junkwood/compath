package com.example.task.dto;

import com.example.task.entity.TaskEntityKJH;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDetailDTOKJH { // 업무 상세
    private Integer taskId;
    private String title;
    private String content;
    private Integer assigneeUserId;
    private String assigneeUserName;
    private String priorityName;
    private Integer progressRate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate estStartDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate estEndDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    private Integer actualHours;
    private Integer estimatedHours;
    private String projectName;
    private String parentProjectName;
    private String typeName;
    private String statusName;
}