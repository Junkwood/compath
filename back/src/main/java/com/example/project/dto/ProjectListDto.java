package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectListDto {

        private Integer projectId;
        private String parentProjectName;
        private LocalDate endDate;
        private Integer pmUserId;
        private Integer plUserId;
        private Integer progressRate;
}
