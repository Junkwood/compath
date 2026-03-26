package com.example.task.mapper;

import com.example.task.dto.TaskListDashDtoJDJ;

import java.util.List;

public interface TaskMapperJDJ {
    //메인대쉬보드 업무 통합 현황용 업무 목록
    List<TaskListDashDtoJDJ> getTaskListDash();
}
