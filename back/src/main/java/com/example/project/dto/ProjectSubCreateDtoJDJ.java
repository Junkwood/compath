package com.example.project.dto;

import lombok.Data;

import java.time.LocalDate;

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
    private LocalDate updatedAt;
    private String editorUserId;
    private String status;
}
