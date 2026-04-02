package com.example.task.service.impl;

import com.example.task.dto.TaskDetailDTOKJH;
import com.example.task.dto.TaskListDTOKJH;
import com.example.task.entity.TaskEntityKJH;
import com.example.task.mapper.TaskMapperKJH;
import com.example.task.service.TaskServiceKJH;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImplKJH implements TaskServiceKJH {

    private final TaskMapperKJH mapper;

    @Override
    public List<TaskListDTOKJH> getAllTasks(String id, String pid) {
        List<TaskEntityKJH> list = mapper.getAllTasks(id, pid);

        return list.stream().map(TaskListDTOKJH::fromTaskEntity).collect(Collectors.toList());
    }

    @Override
    public TaskListDTOKJH getProjectName(Integer id) {

        return mapper.getProjectName(id);
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

        // 활동내역에 등록
//        dto.setTargetId();
//        mapper.registerTimeLog(dto);
        mapper.modifyActualTime(hours,taskId);

        return mapper.getTimeEntriesById(taskId);

    }

    @Override
    public List<TaskDetailDTOKJH> getTimeEntriesById(Integer id) {
        return mapper.getTimeEntriesById(id);
    }
}