package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectCreateDtoJDJ {
    private Integer projectId;
    private String projectName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String isPublic;
    private Integer createdBy;
    private Integer pmUserId;
    private Integer plUserId;
    private String useMilestone;
}
