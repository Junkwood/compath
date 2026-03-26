package com.example.task.service.impl;

import com.example.task.dto.TaskListDashDtoJDJ;
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

    @Override
    public List<TaskListDashDtoJDJ> getTaskListDash() {
        return taskMapperJDJ.getTaskListDash();
    }
}
