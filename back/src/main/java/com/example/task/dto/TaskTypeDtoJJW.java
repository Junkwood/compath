package com.example.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Data
public class TaskTypeDtoJJW {
    // 업무유형 선택용
    private  Long taskTypeId;
    private String typeName;
}
