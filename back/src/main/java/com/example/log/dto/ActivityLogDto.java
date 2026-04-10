package com.example.log.dto;

import lombok.Data;

@Data
public class ActivityLogDto {
    private Integer activityLogId;
    private String targetType;
    private String targetId;
    private String actionType;
    private Integer actorUserId;
    private String userName;
    private String beforeValue;
    private String afterValue;
    private String createdAt;
    private Integer projectId;
}
