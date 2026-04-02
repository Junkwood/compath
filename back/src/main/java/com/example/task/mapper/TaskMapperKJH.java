package com.example.task.mapper;

import com.example.task.dto.TaskDetailDTOKJH;
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

  // 업무 상세
  public TaskDetailDTOKJH getTaskById(Integer id);

  // 소요시간 등록
  public int registerTimeEntries(TaskDetailDTOKJH dto);

  // 소요시간 등록
  public int getSumTimeById(Integer id);

  // 소요시간 등록 건 조회
  public int getTimeEntriesId(Integer id);

  // 소요시간 등록으로 인한 합 수정
  public int modifyActualTime(Integer hours, Integer id);

  // 활동내역 테이블 등록(소요시간)
  public int registerTimeLog(TaskDetailDTOKJH dto);

  // 업무별 소요시간 목록들 조회
   public List<TaskDetailDTOKJH> getTimeEntriesById(Integer id);
}