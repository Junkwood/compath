package com.example.milestone.service.impl;

import com.example.milestone.dto.MilestoneCreateDto;
import com.example.milestone.dto.MilestoneSubProjectDto;
import com.example.milestone.dto.MilestoneTabDto;
import com.example.milestone.dto.MilestoneDto;
import com.example.milestone.mapper.MilestoneMapper;
import com.example.milestone.service.MilestoneService;
import com.example.task.dto.TaskListWnoSubPidDtoJDJ;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class MilestoneServiceImpl implements MilestoneService {

    private final MilestoneMapper milestoneMapper;

    //마일스톤 목록조회
    @Override
    public List<MilestoneDto> getMilestoneListByPid(int projectId) {
        return milestoneMapper.getMilestoneListByPid(projectId);
    }

    //마일스톤 상세 페이지 조회
    @Override
    public List<MilestoneTabDto> getMilestoneTab(int projectId) {
        return milestoneMapper.getMilestoneTab(projectId);
    }

    //마일스톤 별 하위 프로젝트 / 개별업무 조회
    @Override
    public List<MilestoneSubProjectDto> getSubProjectByMid(int projectId, int milestoneId) {
        return milestoneMapper.getSubProjectByMid(projectId, milestoneId);
    }

    //마일스톤은 있고 하위프로젝트는 없는 개별업무 조회
    @Override
    public List<TaskListWnoSubPidDtoJDJ> getTaskListWnoSubPid(int projectId, int milestoneId) {
        return milestoneMapper.getTaskListWnoSubPid(projectId, milestoneId);
    }

    //마일스톤 생성
    @Override
    public void registerMilestone(MilestoneCreateDto dto) {
        milestoneMapper.registerMilestone(dto);
    }


    //마일스톤 수정
    @Override
    public void modifyMilestone(MilestoneCreateDto dto) {
        milestoneMapper.modifyMilestone(dto);
    }

    //마일스톤 삭제 (상태값 업데이트 : E2로)
    @Override
    public void removeMilestoneProc(MilestoneCreateDto dto) {
        milestoneMapper.removeMilestoneProc(dto);
    }
}
