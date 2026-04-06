package com.example.task.service.impl;

import com.example.task.dto.TaskListDashDtoJDJ;
import com.example.task.dto.TaskSummaryDtoJDJ;
import com.example.task.mapper.TaskMapperJDJ;
import com.example.task.service.TaskServiceJDJ;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class TaskServiceImplJDJ implements TaskServiceJDJ {

    final TaskMapperJDJ taskMapperJDJ;

    //메인대쉬보드 업무 통합 현황용 업무 목록
    @Override
    public List<TaskListDashDtoJDJ> getTaskListDash() {
        return taskMapperJDJ.getTaskListDash();
    }

    //하위프로젝트대쉬보드 업무통계 보기
    @Override
    public List<TaskSummaryDtoJDJ> getTaskSummaryByProjectId(int projectId) {
        return taskMapperJDJ.getTaskSummaryByProjectId(projectId);
    }
}
