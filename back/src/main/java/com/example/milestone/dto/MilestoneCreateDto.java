package com.example.milestone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MilestoneCreateDto {
    private Integer milestoneId;
    private Integer createdBy;
    private String milestoneName;
    private String description;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Integer projectId;

}
