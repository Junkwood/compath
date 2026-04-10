package com.example.log.controller;

import com.example.log.dto.ActivityLogDto;
import com.example.log.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ActivityLogController {
    private final ActivityLogService activityLogService;

    @GetMapping("/activityLogs/{projectId}")
    public List<ActivityLogDto> getActivityLogs(@PathVariable Integer projectId) {
        return activityLogService.getActivityLogsByProjectId(projectId);
    }
}
