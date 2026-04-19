package com.example.log.dto;

import lombok.Data;

@Data
public class ActivityLogSearchDto {
    private Integer projectId;
    private String searchKeyword;
    private String activityType;
    private String targetType;
    private String startDate;
    private String endDate;
    private String sortOrder;
}