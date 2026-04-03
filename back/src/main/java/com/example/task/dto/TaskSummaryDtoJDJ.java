package com.example.task.dto;

import lombok.Data;

@Data
public class TaskSummaryDtoJDJ {
    private String type;
    private int total;
    private int inProgress;
    private int done;
    private int rejected;
    private int totalSum;
}
