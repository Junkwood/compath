package com.example.task.service;

import com.example.task.dto.TaskListDashDtoJDJ;
import com.example.task.dto.TaskSummaryDtoJDJ;

import java.util.List;

public interface TaskServiceJDJ {
    //메인대쉬보드 업무 통합 현황용 업무 목록
    List<TaskListDashDtoJDJ> getTaskListDash();

    //하위프로젝트대쉬보드 업무통계 보기
    List<TaskSummaryDtoJDJ> getTaskSummaryByProjectId(int projectId);
}
