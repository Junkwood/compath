package com.example.milestone.service.impl;

import com.example.milestone.dto.MilestoneDto;
import com.example.milestone.mapper.MilestoneMapper;
import com.example.milestone.service.MilestoneService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class MilestoneServiceImpl implements MilestoneService {

    private final MilestoneMapper milestoneMapper;

    @Override
    public List<MilestoneDto> getMilestoneListByPid(int projectId) {
        return milestoneMapper.getMilestoneListByPid(projectId);
    }
}
