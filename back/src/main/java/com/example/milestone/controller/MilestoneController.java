package com.example.milestone.controller;

import com.example.milestone.dto.MilestoneDto;
import com.example.milestone.service.MilestoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class MilestoneController {

    private final MilestoneService milestoneService;

    @GetMapping("/MilestoneListByPid/{projectId}")
    public List<MilestoneDto> mlist(@PathVariable Integer projectId){
        return milestoneService.getMilestoneListByPid(projectId);
    }
}
