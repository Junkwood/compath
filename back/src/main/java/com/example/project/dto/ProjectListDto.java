package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectListDto {
    private Integer projectId;
    private String projectName;
    private String identifier;
    private String startDate;
    private String endDate;

    private Integer pmUserId;
    private Integer plUserId;

    private String pmName;
    private String plName;

    private String isPublic;
    private String useMilestone;
    private String isMyProject;  //추가
    
    private Integer progressRate;
    private String createdAt;
}