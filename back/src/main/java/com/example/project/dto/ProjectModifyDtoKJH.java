package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectModifyDtoKJH {
    private Integer projectId;
    private String identifier;
    private String projectName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String isPublic;
    private String plUserId;
    private String useMilestone;
    private Integer editorUserId;
}
