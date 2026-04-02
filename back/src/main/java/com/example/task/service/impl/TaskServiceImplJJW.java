package com.example.task.service.impl;

import com.example.task.dto.*;
import com.example.task.mapper.TaskMapperJJW;
import com.example.task.service.TaskServiceJJW;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class TaskServiceImplJJW implements TaskServiceJJW {

    private final TaskMapperJJW taskMapperJJW;


    //업무등록
    @Override
    public void insert(TaskReqDtoJJW dto) {
        taskMapperJJW.insert(dto);
    }
    //업무 수정
    @Override
    public int updateTask(TaskReqDtoJJW dto) {
        // 시작일 자동 세팅 (상태가 '진행중'인 경우)
        if (dto.getTaskStatusId() != null && dto.getTaskStatusId() == 2) {
            if (dto.getStartDate() == null) {
                dto.setStartDate(new Date());
            }
        }
        // 소요 시간 자동 계산 (일수 * 8시간)
        if (dto.getStartDate() != null && dto.getDueDate() != null) {
            long diff = dto.getDueDate().getTime() - dto.getStartDate().getTime();
            long diffDays = (diff / (1000 * 60 * 60 * 24)) + 1;
            dto.setActualHours(String.valueOf(diffDays * 8));
        }
        return taskMapperJJW.updateTask(dto);
    }
    //반려 사유 등록
    @Override
    public void insert1(TaskRejectDtoJJW re) {
        taskMapperJJW.insert1(re);
        taskMapperJJW.updateTaskStatus(re.getTaskId(), 4);
    }


    //업무 전체 조회
    @Override
    public List<TaskReqDtoJJW> getTaskAll() {
        return taskMapperJJW.getTaskAll();
    }


    //프로시저 사용
    @Override
    public Map<String, Object> getTaskInitData(Integer taskId, Integer projectId) {
        // 프로시저에 전달할 파라미터 맵
        Map<String, Object> params = new HashMap<>();
        params.put("taskId", taskId);
        params.put("projectId", projectId);

        // OUT 파라미터들이 params 맵에 들어감 ?
        taskMapperJJW.getTaskTotalInfo(params);

        // 커서 결과 값으로 보내기
        Map<String, Object> result = new HashMap<>();
        result.put("taskDetail", params.get("taskDetail"));
        result.put("projectList", params.get("projectList"));
        result.put("userList", params.get("userList"));
        result.put("taskTypeList", params.get("taskTypeList"));
        result.put("milestoneList", params.get("milestoneList"));

        return result;
    }
}