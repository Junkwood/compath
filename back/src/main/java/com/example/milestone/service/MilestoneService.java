package com.example.milestone.service;

import com.example.milestone.dto.MilestoneCreateDto;
import com.example.milestone.dto.MilestoneSubProjectDto;
import com.example.milestone.dto.MilestoneTabDto;
import com.example.milestone.dto.MilestoneDto;
import com.example.milestone.mapper.MilestoneMapper;
import com.example.task.dto.TaskListWnoSubPidDtoJDJ;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface MilestoneService {
    
    //마일스톤 목록조회
    public List<MilestoneDto> getMilestoneListByPid(int projectId);

    //마일스톤 탭용 조회
    public List<MilestoneTabDto> getMilestoneTab(int projectId);

    //마일스톤 별 하위 프로젝트 / 개별업무 조회
    public List<MilestoneSubProjectDto> getSubProjectByMid(int projectId, int milestoneId);

    //마일스톤은 있고 하위프로젝트는 없는 개별업무 조회
    public List<TaskListWnoSubPidDtoJDJ> getTaskListWnoSubPid(int projectId, int milestoneId);

    //마일스톤 생성
    void registerMilestone(MilestoneCreateDto dto);

    //마일스톤 수정
    void modifyMilestone(MilestoneCreateDto dto);

    //마일스톤 삭제 (상태값 업데이트 : E2로)
    void removeMilestoneProc(MilestoneCreateDto dto);

}
