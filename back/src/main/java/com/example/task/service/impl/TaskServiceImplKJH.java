package com.example.task.service.impl;

import com.example.task.dto.TaskDetailDTOKJH;
import com.example.task.dto.TaskListDTOKJH;
import com.example.task.dto.TaskReqDtoJJW;
import com.example.task.mapper.TaskMapperJJW;
import com.example.task.mapper.TaskMapperKJH;
import com.example.task.service.TaskServiceKJH;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImplKJH implements TaskServiceKJH {

    private final TaskMapperKJH mapper;

    @Override
    public List<TaskListDTOKJH> getAllTasks(TaskListDTOKJH dto) {
        List<TaskListDTOKJH> list = mapper.getAllTasks(dto);
        if(!list.isEmpty()) {
            return list;
        } else {
            return Collections.emptyList();
        }

    }

    // 필터링 조건 조회(PL/SQL)
    public Map<String, Object> getAllFiterInfo(Integer id) {
        Map<String, Object> params = new HashMap<>();
        params.put("projectId", id);
        mapper.getAllFilterList(params);

        List<TaskListDTOKJH> member = mapper.getProjectDeveloper(id);

        Map<String, Object> result = new HashMap<>();
        result.put("taskTitleList", params.get("taskTitleList"));
        result.put("userNameList", params.get("userNameList"));
        result.put("taskTypeList", params.get("taskTypeList"));
        result.put("taskStatusList", params.get("taskStatusList"));
        result.put("taskPriorityList", params.get("taskPriorityList"));
        result.put("smallProjectList", params.get("smallProjectList"));
        result.put("developerList", member);

        return result;
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

    @Override
    public List<TaskDetailDTOKJH> getAllTaskType() {
        return mapper.getAllTaskType();
    }

    @Override
    public int modifyTaskStatus(Integer id) {
        return mapper.modifyTaskStatus(id);
    }

    @Override
    public int modifyTaskUser(TaskListDTOKJH dto) {
        return mapper.modifyTaskUser(dto);
    }

}