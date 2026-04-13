package com.example.task.mapper;

import com.example.task.dto.TaskDetailDTOKJH;
import com.example.task.dto.TaskListDTOKJH;
import com.example.task.dto.TaskReqDtoJJW;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TaskMapperKJH {
  // 업무 목록
  public List<TaskListDTOKJH> getAllTasks(TaskListDTOKJH dto);

  // 프로젝트 이름
  public TaskListDTOKJH getProjectName(Integer id);

  // 업무 상세
  public TaskDetailDTOKJH getTaskById(Integer id);

  // 소요시간 등록
  public int registerTimeEntries(TaskDetailDTOKJH dto);

  // 소요시간 등록
  public int getSumTimeById(Integer id);

  // 소요시간 등록 건 조회(소요시간 등록 후)
  public String getRecentRegisteredTime(Integer id);

  // 소요시간 등록으로 인한 합 수정
  public int modifyActualTime(Integer hours, Integer id);

  // 활동내역 테이블 등록(소요시간)
  public int registerTimeLog(TaskDetailDTOKJH dto);

  // 업무별 소요시간 목록들 조회
   public List<TaskDetailDTOKJH> getTimeEntriesById(Integer id);

   // 활동내역 목록조회
   public List<TaskDetailDTOKJH> getTimeLog(Integer id);


//
//  백에서 페이지네이션 도전
//  업무 총 건수
  public int getAllTaskCount(Integer id);

//  필터링 조건들 조회(PL/SQL)
  public void getAllFilterList(Map<String, Object> params);


  // 추천 업무 등록(회의록)
  void insert(TaskDetailDTOKJH vo);
}