package com.example.task.service.impl;

import com.example.task.dto.TaskDetailDTOKJH;
import com.example.task.dto.TaskListDTOKJH;
import com.example.task.mapper.TaskMapperKJH;
import com.example.task.service.TaskServiceKJH;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImplKJH implements TaskServiceKJH {

    private final TaskMapperKJH mapper;

    @Override
    public List<TaskListDTOKJH> getAllTasks(Integer id, Integer pid, Integer startNum, Integer endNum) {
        return mapper.getAllTasks(id, pid, startNum, endNum);
    }

    @Override
    public TaskListDTOKJH getProjectName(Integer id) {
        int count = mapper.getAllTaskCount(id);

        TaskListDTOKJH dto = mapper.getProjectName(id);

        dto.setTaskCounts(count);

        return dto;
    }

    @Override
    public TaskDetailDTOKJH getTaskById(Integer id) {
        return mapper.getTaskById(id);
    }

    @Override
    public List<TaskDetailDTOKJH> registerTimeEntries(TaskDetailDTOKJH dto) {
        // 소요시간 테이블에 등록
        mapper.registerTimeEntries(dto);

        // 소요시간 합계 조회
        Integer taskId = dto.getTaskId();
        Integer sum = mapper.getSumTimeById(taskId);
        dto.setSum(sum);

        // 소요시간 합계로 수정
        Integer hours = dto.getSum();
        mapper.modifyActualTime(hours,taskId);

        // 소요시간 최신 등록 건 조회
        String recentId = mapper.getRecentRegisteredTime(taskId);

        // 활동내역에 등록
        dto.setTargetId(recentId);
        dto.setActorUserId(dto.getUserId());
        dto.setBeforeValue(dto.getBeforeValue());
        dto.setAfterValue(Integer.toString(dto.getSum()));
        mapper.registerTimeLog(dto);

        return mapper.getTimeEntriesById(taskId);
    }

    @Override
    public List<TaskDetailDTOKJH> getTimeEntriesById(Integer id) {
        return mapper.getTimeEntriesById(id);
    }

    @Override
    public List<TaskDetailDTOKJH> getTimeLog(Integer id) {
        return mapper.getTimeLog(id);
    }

    //
}