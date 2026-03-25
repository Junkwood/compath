package com.example.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Data
public class ProjectDtoJJW {
    // 프로젝트 선택 팝업용
    private String projectId;
    private String projectName;

}
