package com.example.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class kjhTaskDTO {
    private String currentTime;
    private String message;
    private int statusCode;
}