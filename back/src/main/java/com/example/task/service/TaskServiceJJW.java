package com.example.task.service;

import com.example.task.dto.*;

import java.util.List;


public interface TaskServiceJJW {
    //상위 업무 생성(등록)
    void insert(TaskReqDtoJJW dto);

    //선택 모달 조회
    List<ProjectDtoJJW> getProjectList();
    List<UserDtoJJW> getUserList();
    List<TaskTypeDtoJJW> getTaskType();
    List<TaskStatusDtoJJW> getTaskStatus();
    List<MilestoneDtoJJW> getMilestone();

}
