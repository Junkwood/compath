package com.example.project.dto;

import lombok.Data;

@Data
public class ProjectCopyRequestDto {
    private Integer sourceProjectId;

    private String projectName;
    private String description;
    private String startDate;
    private String endDate;
    private String isPublic;
    private String useMilestone;

    private Integer pmUserId;
    private Integer plUserId;
    private Integer createdBy;

    private String copyMembers;     // Y / N
    private String copyMilestones;  // Y / N
    private String copyTasks;       // Y / N
}