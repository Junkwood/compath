package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTOJDJ {

        private String projectId;
        private String parentProjectId;
        private String projectName;
        private String description;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;
        private String isPublic;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private int createdBy;
        private int pmUserId;
        private int plUserId;
        private int editorUserId;
}
