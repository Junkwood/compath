package com.example.task.mapper;

import com.example.milestone.dto.MilestoneDtoJJW;
import com.example.project.dto.ProjectDtoJJW;
import com.example.task.dto.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TaskMapperJJW {

    //상위 업무 생성(등록)
    void insert(TaskReqDtoJJW vo);
    //상위 업무 수정
    int updateTask(TaskReqDtoJJW dto);

    //업무 상세 조회
    TaskReqDtoJJW getTaskById(Integer taskId);


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
}
