package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectSingleDtoKJH {
    private String projectId;
    private String identifier;
    private String parentProjectId;
    private String projectName;
    private String description;
    private String startDate;
    private String endDate;
    private String status;
    private String isPublic;
    private String createdAt;
    private String updatedAt;
    private String createdBy;
    private String pmUserId;
    private String plUserId;
    private String editorUserId;
    private String useMilestone;
}
