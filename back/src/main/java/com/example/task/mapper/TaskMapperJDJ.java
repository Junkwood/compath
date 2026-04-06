package com.example.task.mapper;

import com.example.task.dto.TaskListMainDashDtoJDJ;
import com.example.task.dto.TaskListSubDashDtoJDJ;
import com.example.task.dto.TaskSummaryDtoJDJ;

import java.util.List;

public interface TaskMapperJDJ {
    //메인대쉬보드 업무 통합 현황용 업무 목록
    List<TaskListMainDashDtoJDJ> getTaskListDash();

    //하위프로젝트대쉬보드 업무통계 보기
    List<TaskSummaryDtoJDJ> getTaskSummaryByProjectId(int projectId);

    //하위프로젝트대쉬보드 업무목록 조회
    List<TaskListSubDashDtoJDJ> getTaskListBySubProjectId(int subProjectId);

}
