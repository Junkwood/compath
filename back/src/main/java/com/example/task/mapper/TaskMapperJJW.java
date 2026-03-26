package com.example.task.mapper;

import com.example.milestone.dto.MilestoneDtoJJW;
import com.example.project.dto.ProjectDtoJJW;
import com.example.task.dto.*;

import java.util.List;

public interface TaskMapperJJW {

    //업무 생성(등록)
    void insert(TaskReqDtoJJW vo);
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
    List<MilestoneDtoJJW> getMilestone();
}
