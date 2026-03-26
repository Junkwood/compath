package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ProjectDtoJJW {
    // 프로젝트 선택 팝업용
    private String projectId;
    private String projectName;
    private String displayProjectName;
    private String displaySubProjectName;
    private String parentProjectId;

}
