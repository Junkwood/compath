package com.example.milestone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneTabDto {
    private String milestoneName;
    private Integer milestoneId;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Integer parentProjectId;
    private String description;
}
