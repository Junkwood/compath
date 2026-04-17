package com.example.task.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class TaskLogDto {
    private Integer activityLogId;
    private String targetType;
    private String targetId;
    private String actionType;
    private Integer actorUserId;
    private String userName;
    private String beforeValue;
    private String afterValue;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate createdAt;

    private Integer projectId;
}
