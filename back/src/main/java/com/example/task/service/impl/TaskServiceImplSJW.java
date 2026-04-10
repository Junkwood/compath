package com.example.task.service.impl;

import com.example.task.dto.TaskDTOSJW;
import com.example.task.dto.TaskStatusDTOSJW;
import com.example.task.dto.TaskTypeDTOSJW;
import com.example.task.mapper.TaskMapperSJW;
import com.example.task.service.TaskServiceSJW;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImplSJW implements TaskServiceSJW {
    final TaskMapperSJW taskMapper;

    //업무 상태 목록
    @Override
    public List<TaskStatusDTOSJW> getTaskStatuses() {
        return taskMapper.getTaskStatuses();
    }

    //활성화된 업무 상태 목록
    @Override
    public List<TaskStatusDTOSJW> getActiveTaskStatuses() {
        return taskMapper.getActiveTaskStatuses();
    }

    //업무 상태 활성여부 수정
    @Override
    public Boolean modifyTaskStatusActivation(TaskStatusDTOSJW taskStatus) {
        log.error(taskStatus.toString());
        if (taskStatus.getIsActive().equals("Y")) {
            taskStatus.setIsActive("O1");
        } else {
            taskStatus.setIsActive("O2");
        }
        Integer result = taskMapper.modifyTaskStatus(taskStatus);
        if (result > 0) {
            return true;
        }
        return false;
    }

    //업무 상태 종료 여부 수정
    @Override
    public Boolean modifyTaskStatusIsFinal(TaskStatusDTOSJW taskStatus) {
        log.error(taskStatus.toString());
        if (taskStatus.getIsFinal().equals("Y")) {
            taskStatus.setIsFinal("O1");
        } else {
            taskStatus.setIsFinal("O2");
        }
        Integer result = taskMapper.modifyTaskStatus(taskStatus);
        if (result > 0) {
            return true;
        }
        return false;
    }

    //업무 상태 수정
    @Override
    public Boolean modifyTaskStatus(TaskStatusDTOSJW taskStatus) {
        log.error(taskStatus.toString());
        if (taskStatus.getIsActive().equals("Y")) {
            taskStatus.setIsActive("O1");
        } else {
            taskStatus.setIsActive("O2");
        }
        if (taskStatus.getIsFinal().equals("Y")) {
            taskStatus.setIsFinal("O1");
        } else {
            taskStatus.setIsFinal("O2");
        }
        Integer result = taskMapper.modifyTaskStatus(taskStatus);
        if (result > 0) {
            return true;
        }
        return false;
    }

    //업무 상태 등록
    @Override
    public Boolean registerTaskStatus(TaskStatusDTOSJW taskStatus) {
        if (taskStatus.getIsActive().equals("Y")) {
            taskStatus.setIsActive("O1");
        } else {
            taskStatus.setIsActive("O2");
        }
        if (taskStatus.getIsFinal().equals("Y")) {
            taskStatus.setIsFinal("O1");
        } else {
            taskStatus.setIsFinal("O2");
        }
        Integer result = taskMapper.registerTaskStatus(taskStatus);
        if (result > 0) {
            return true;
        }
        return false;
    }

    //업무 상태 이름 중복체크
    @Override
    public Boolean checkDup(String name) {
        Integer result = taskMapper.checkDup(name);
        if (result > 0) {
            return false;
        }
        return true;
    }

    //업무 유형 목록
    @Override
    public List<TaskTypeDTOSJW> getTaskTypes() {
        return taskMapper.getTaskTypes();
    }

    // 활성화된 업무 유형 목록
    @Override
    public List<TaskTypeDTOSJW> getActiveTaskTypes() {
        return taskMapper.getActiveTaskTypes();
    }

    @Override
    public Boolean modifyTaskTypeActivation(TaskTypeDTOSJW taskType) {
        if (taskType.getIsActive().equals("Y")) {
            taskType.setIsActive("O1");
        } else {
            taskType.setIsActive("O2");
        }
        Integer result = taskMapper.modifyTaskType(taskType);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean checkDupType(String name) {
        Integer result = taskMapper.checkDupType(name);
        if (result > 0) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean registerTaskType(TaskTypeDTOSJW taskType) {
        log.error(taskType.toString());
        if (taskType.getIsActive().equals("Y")) {
            taskType.setIsActive("O1");
        } else {
            taskType.setIsActive("O2");
        }
        Integer result = taskMapper.registerTaskType(taskType);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean modifyTaskType(Integer taskTypeId, TaskTypeDTOSJW taskType) {
        if (taskType.getIsActive()!=null) {
            if (taskType.getIsActive().equals("Y")) {
                taskType.setIsActive("O1");
            } else {
                taskType.setIsActive("O2");
            }
        }
        taskType.setTaskTypeId(taskTypeId);
        log.error(taskType.toString());
        Integer result = taskMapper.modifyTaskType(taskType);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<TaskDTOSJW> getTaskList(Integer userId) {
        return taskMapper.getTaskList(userId);
    }

    @Override
    public List<TaskDTOSJW> getTaskSummary(Integer userId) {
        return taskMapper.getTaskSummary(userId);
    }
}
