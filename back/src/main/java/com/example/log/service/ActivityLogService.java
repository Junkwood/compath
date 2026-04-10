package com.example.log.service;

import com.example.log.dto.ActivityLogDto;

import java.util.List;

public interface ActivityLogService {
    List<ActivityLogDto> getActivityLogsByProjectId(Integer projectId);
}
