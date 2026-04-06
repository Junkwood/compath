package com.example.project.dto;

import lombok.Data;

@Data
public class ProjectSubCreateDtoJDJ {
    private Integer parentProjectId;
    private Integer milestoneId;
    private String projectName;
    private String identifier;
    private Integer subPlUserId;
    private String startDate;
    private String endDate;
    private String description;
    private String useMilestone;
    private String isPublic;
    private Integer userId;
    private Integer projectId;
    private Integer milestoneMappingId;
}
