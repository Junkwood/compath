package com.example.task.mapper;

import com.example.milestone.dto.MilestoneDtoJJW;
import com.example.project.dto.ProjectDtoJJW;
import com.example.task.dto.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface TaskMapperJJW {

    //상위 업무 생성(등록)
    void insert(TaskReqDtoJJW vo);
    //상위 업무 수정
    int updateTask(TaskReqDtoJJW dto);

    //업무 상세 조회
    TaskReqDtoJJW getTaskById(Integer taskId);

    //업무 전체 조회
    List<TaskReqDtoJJW> getTaskAll();


    void getTaskTotalInfo(Map<String, Object> params);

    //사유등록
    void insert1 (TaskRejectDtoJJW re);

    //프로젝트 조회
    List<ProjectDtoJJW> getProjectList();
    //프로젝트 상세 조회
    ProjectDtoJJW getProjectDetail(Integer projectId);
    //담당자 조회
    List<UserDtoJJW> getUserList();
    //업무 유형 조회
    List<TaskTypeDtoJJW> getTaskType();
    //업무 상태 조회
    List<TaskStatusDtoJJW> getTaskStatus();
    //마일스톤 선택용
    List<MilestoneDtoJJW> getMilestone(@Param("projectId") Integer projectId);

    //모달 반려
    void updateTaskStatus(@Param("taskId") Integer taskId, @Param("status") int status);}
