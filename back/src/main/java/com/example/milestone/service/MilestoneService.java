package com.example.milestone.service;

import com.example.milestone.dto.MilestoneDto;

import java.util.List;

public interface MilestoneService {
    
    //마일스톤 목록조회
    public List<MilestoneDto> getMilestoneListByPid(int projectId);
}
