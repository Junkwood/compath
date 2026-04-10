package com.example.log.service.impl;

import com.example.log.dto.ActivityLogDto;
import com.example.log.mapper.ActivityLogMapper;
import com.example.log.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogMapper activityLogMapper;

    @Override
    public List<ActivityLogDto> getActivityLogsByProjectId(Integer projectId) {
        return activityLogMapper.getActivityLogsByProjectId(projectId);
    }
}
