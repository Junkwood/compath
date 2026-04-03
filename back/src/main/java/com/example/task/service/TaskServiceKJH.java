package com.example.task.service;

import com.example.task.dto.TaskDetailDTOKJH;
import com.example.task.dto.TaskListDTOKJH;

import java.util.List;

public interface TaskServiceKJH {

    // 업무 목록
    public List<TaskListDTOKJH> getAllTasks(Integer id, Integer pid, Integer startNum, Integer endNum);

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


}