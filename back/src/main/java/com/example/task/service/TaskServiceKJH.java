package com.example.task.service;

import com.example.task.dto.TaskDetailDTOKJH;
import com.example.task.dto.TaskListDTOKJH;
import com.example.task.dto.TaskLogDto;
import com.example.task.dto.TaskReqDtoJJW;

import java.util.List;
import java.util.Map;

public interface TaskServiceKJH {

    // 업무 목록
    public List<TaskListDTOKJH> getAllTasks(TaskListDTOKJH dto);

    // 필터링 조건 조회(PL/SQL)
    public Map<String, Object> getAllFiterInfo(Integer id);

    // 프로젝트 이름
    public TaskListDTOKJH getProjectName(Integer id);

    // 업무 상세
    public TaskDetailDTOKJH getTaskById(Integer id);

    // 소요시간 등록
    public List<TaskDetailDTOKJH> registerTimeEntries(TaskDetailDTOKJH dto);

    // 업무별 소요시간 목록들 조회
    public List<TaskDetailDTOKJH> getTimeEntriesById(Integer id);

    // 활동내역 목록조회
    public List<TaskDetailDTOKJH> getTimeLog(Integer id);

    //  공지사항 및 문서 작성 시 카테고리
    public List<TaskDetailDTOKJH> getAllTaskType();

    //  업무 비활성화
    public int  modifyTaskStatus(Integer id);

    //  업무 담당자 지정(미지정)
    public int  modifyTaskUser(TaskListDTOKJH dto);

    //  업무 작업 이력
    public List<TaskLogDto> getActivityLogsByTaskId(Integer id);

}