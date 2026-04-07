package com.example.task.mapper;

import com.example.task.dto.TaskStatusDTOSJW;
import com.example.task.dto.TaskTypeDTOSJW;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskMapperSJW {
    // 업무 상태 목록
    List<TaskStatusDTOSJW> getTaskStatuses();

    // 업무 상태 수정
    Integer modifyTaskStatus(TaskStatusDTOSJW taskStatus);

    // 업무 상태 등록
    Integer registerTaskStatus(TaskStatusDTOSJW taskStatus);

    //업무 상태 이름 중복 체크
    Integer checkDup(String id);

    // 업무 유형 목록
    List<TaskTypeDTOSJW> getTaskTypes();
}