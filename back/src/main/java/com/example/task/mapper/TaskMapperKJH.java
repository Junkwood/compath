package com.example.task.mapper;

import com.example.task.dto.TaskListDTOKJH;
import com.example.task.entity.TaskEntityKJH;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskMapperKJH {
  // 업무 목록
  public List<TaskEntityKJH> getAllTasks(String id, String pid);

  // 프로젝트 이름
  public TaskListDTOKJH getProjectName(Integer id);

}