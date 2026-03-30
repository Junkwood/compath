package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectSingleDtoKJH {
    private Integer projectId;
    private String identifier;
    private Integer parentProjectId;
    private String projectName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String isPublic;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Integer createdBy;
    private String pmUserId;
    private String plUserId;
    private Integer editorUserId;
    private String useMilestone;
}
