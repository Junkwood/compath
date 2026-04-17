package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyTaskSummaryDto {
    private int created;
    private int inProgress;
    private int done;
    private int rejected;
    private int deadline;
}