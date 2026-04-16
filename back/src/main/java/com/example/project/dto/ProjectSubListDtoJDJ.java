package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSubListDtoJDJ {
    private String rootProjectName;
    private Integer projectId;
    private String projectName;
    private Integer plUserId;
    private Integer milestoneId;
    private String milestoneName;
    private String userName;
    private String identifier;
    private LocalDate startDate;
    private LocalDate endDate;
}
