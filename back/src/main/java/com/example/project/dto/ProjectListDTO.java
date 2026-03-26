package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectListDTO {

        private String projectId;
        private String parentProjectId;
        private String projectName;
        private LocalDate endDate;
        private int pmUserId;
        private int plUserId;
        private String progressRate;
}
