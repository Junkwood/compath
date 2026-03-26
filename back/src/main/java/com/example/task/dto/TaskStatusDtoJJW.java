package com.example.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Data
public class TaskStatusDtoJJW {
    // 업무상태 선택용
    private Long taskStatusId;
    private String statusName;
}
