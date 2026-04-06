package com.example.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskListMainDashDtoJDJ {
    //메인대쉬보드 업무 통합 현황용 업무 목록
    private Integer projectId;
    private String projectName;
    private Integer created;
    private Integer inProgress;
    private Integer devDone;
    private Integer rejected;
    private Integer done;
    private Integer totalSum;
}
