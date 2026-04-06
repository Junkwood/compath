package com.example.task.service.impl;

import com.example.task.dto.TaskStatusDTOSJW;
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
    @Override
    public List<TaskStatusDTOSJW> getTaskStatuses() {
        return taskMapper.getTaskStatuses();
    }

    @Override
    public Boolean modifyTaskStatusActivation(TaskStatusDTOSJW taskStatus) {
        log.error(taskStatus.toString());
        if(taskStatus.getIsActive().equals("Y")){
            taskStatus.setIsActive("O1");
        }else{
            taskStatus.setIsActive("O2");
        }
        Integer result = taskMapper.modifyTaskStatus(taskStatus);
        if(result > 0){
            return true;
        }
        return false;
    }

    @Override
    public Boolean modifyTaskStatusIsFinal(TaskStatusDTOSJW taskStatus) {
        log.error(taskStatus.toString());
        if(taskStatus.getIsFinal().equals("Y")){
            taskStatus.setIsFinal("O1");
        }else{
            taskStatus.setIsFinal("O2");
        }
        Integer result = taskMapper.modifyTaskStatus(taskStatus);
        if(result > 0){
            return true;
        }
        return false;
    }

    @Override
    public Boolean modifyTaskStatus(TaskStatusDTOSJW taskStatus) {
        Integer result = taskMapper.modifyTaskStatus(taskStatus);
        if(result > 0){
            return true;
        }
        return false;
    }

    @Override
    public Boolean registerTaskStatus(TaskStatusDTOSJW taskStatus) {
        if(taskStatus.getIsActive().equals("Y")){
            taskStatus.setIsActive("O1");
        }else{
            taskStatus.setIsActive("O2");
        }
        if(taskStatus.getIsFinal().equals("Y")){
            taskStatus.setIsFinal("O1");
        }else{
            taskStatus.setIsFinal("O2");
        }
        Integer result = taskMapper.registerTaskStatus(taskStatus);
        if(result > 0){
            return true;
        }
        return false;
    }

    @Override
    public Boolean checkDup(String name) {
        Integer result = taskMapper.checkDup(name);
        if(result > 0){
            return true;
        }
        return false;
    }
}
