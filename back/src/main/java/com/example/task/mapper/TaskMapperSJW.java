package com.example.task.mapper;

import com.example.task.dto.TaskStatusDTOSJW;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskMapperSJW {
  // 업무 상태 목록
  List<TaskStatusDTOSJW> getTaskStatuses();
  // 업무 상태 수정
  Integer modifyTaskStatus(TaskStatusDTOSJW taskStatus);

  Integer registerTaskStatus(TaskStatusDTOSJW taskStatus);

  Integer checkDup(String id);
}