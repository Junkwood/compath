package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectSingleDtoJDJ {
    private Integer projectId;
    private String projectName;
    private LocalDate startDate;
    private LocalDate endDate;
}
