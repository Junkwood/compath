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
    private String projectName;
    private String identifier;
    private String startDate;
    private String endDate;
    private String pmName;
    private String plName;
    private Integer progressRate;
}
