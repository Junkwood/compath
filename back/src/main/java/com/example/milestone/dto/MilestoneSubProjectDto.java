package com.example.milestone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneSubProjectDto {
    private Integer projectId;
    private String projectName;
    private Integer subPlUserId;
    private Integer milestoneId;
    private String milestoneName;
    private String userName;
    private LocalDate startDate;
    private LocalDate endDate;
}
