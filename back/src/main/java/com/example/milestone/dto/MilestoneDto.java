package com.example.milestone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MilestoneDto {
    private Integer milestoneId;
    private String milestoneName;
    private LocalDate dueDate;
    private Integer projectId;
    private String description;
    private String status;
    private Integer totalTasks;
    private Integer completedTasks;
    private Integer inprogressTasks;
    private Integer progressRate;
}
