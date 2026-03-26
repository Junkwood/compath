package com.example.task.service.impl;

import com.example.task.dto.*;
import com.example.task.mapper.TaskMapperJJW;
import com.example.task.service.TaskServiceJJW;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class TaskServiceImpl implements TaskServiceJJW {

    final TaskMapperJJW taskMapperJJW;

    //기본 업무 등록 -상위
    @Override
    public void insert(TaskReqDtoJJW dto) {
        taskMapperJJW.insert(dto);
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
    public List<MilestoneDtoJJW> getMilestone() {
        return taskMapperJJW.getMileStone();
    }
}
