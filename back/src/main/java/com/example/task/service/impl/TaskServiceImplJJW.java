package com.example.task.service.impl;

import com.example.milestone.dto.MilestoneDtoJJW;
import com.example.project.dto.ProjectDtoJJW;
import com.example.task.dto.*;
import com.example.task.mapper.TaskMapperJJW;
import com.example.task.service.TaskServiceJJW;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class TaskServiceImplJJW implements TaskServiceJJW {

    final TaskMapperJJW taskMapperJJW;

    //기본 업무 등록 -상위
    @Override
    public void insert(TaskReqDtoJJW dto) {
        taskMapperJJW.insert(dto);
    }

    @Override
    public int updateTask(TaskReqDtoJJW dto) {
        // 시작일 자동 세팅
        if (dto.getTaskStatusId() != null && dto.getTaskStatusId() == 2) {
            if (dto.getStartDate() == null) {
                dto.setStartDate(new Date());
            }
        }
        //소요 시간 자동 계산
        if (dto.getStartDate() != null && dto.getDueDate() != null) {
            long diff = dto.getDueDate().getTime() - dto.getStartDate().getTime();
            long diffDays = (diff / (1000 * 60 * 60 * 24)) + 1;

            dto.setActualHours(String.valueOf(diffDays * 8));
        }
        return taskMapperJJW.updateTask(dto);
    }

    //업무 상세조회(수정 페이지에 뿌릴려고)
    @Override
    public TaskReqDtoJJW getTaskById(Integer taskId) {
        return taskMapperJJW.getTaskById(taskId);
    }

    @Override
    public List<ProjectDtoJJW> getProjectList() {
        return taskMapperJJW.getProjectList();
    }

    @Override
    public List<UserDtoJJW> getUserList() {
        return taskMapperJJW.getUserList();
    }

    @Override
    public List<TaskTypeDtoJJW> getTaskType() {
        return taskMapperJJW.getTaskType();
    }

    @Override
    public List<TaskStatusDtoJJW> getTaskStatus() {
        return taskMapperJJW.getTaskStatus();
    }

    @Override
    public List<MilestoneDtoJJW> getMilestone(Integer projectId) {
        return taskMapperJJW.getMilestone(projectId);
    }

    @Override
    public ProjectDtoJJW getProjectDetail(Integer projectId) {
        return taskMapperJJW.getProjectDetail(projectId);
    }


}
