package com.example.task.service;

import com.example.milestone.dto.MilestoneDtoJJW;
import com.example.project.dto.ProjectDtoJJW;
import com.example.task.dto.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;


public interface TaskServiceJJW {
    //상위 업무 생성(등록)
    void insert(TaskReqDtoJJW dto);
    //상위 업무 수정
    int updateTask(TaskReqDtoJJW dto);
    //업무 전체 조회
    List<TaskReqDtoJJW> getTaskAll();

    //업무 상세 조회
    TaskReqDtoJJW getTaskById(Integer taskId);
    //사유등록
    void insert1 (TaskRejectDtoJJW re);


    //선택 모달 조회
    List<ProjectDtoJJW> getProjectList();
    List<UserDtoJJW> getUserList();
    List<TaskTypeDtoJJW> getTaskType();
    List<TaskStatusDtoJJW> getTaskStatus();
    List<MilestoneDtoJJW> getMilestone(Integer projectId);

    //프로젝트 상세 조회
    ProjectDtoJJW getProjectDetail(Integer projectId);

    Map<String, Object> getTaskInitData(Integer taskId, Integer projectId);
}
