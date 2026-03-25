package com.example.task.service.impl;

import com.example.task.dto.TaskReqDtoJJW;
import com.example.task.mapper.TaskMapperJJW;
import com.example.task.service.TaskServiceJJW;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
