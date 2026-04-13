package com.example.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TaskTypeDto {
    private Integer taskTypeId;
    private String  typeName;

}
