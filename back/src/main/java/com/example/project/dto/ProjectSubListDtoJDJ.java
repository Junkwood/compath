package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSubListDtoJDJ {
    private Integer projectId;
    private String projectName;
    private Integer plUserId;
    private Integer milestoneId;
    private String milestoneName;
    private String userName;
}
