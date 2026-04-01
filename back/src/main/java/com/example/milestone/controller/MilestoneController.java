package com.example.milestone.controller;

import com.example.milestone.dto.MilestoneSubProjectDto;
import com.example.milestone.dto.MilestoneTabDto;
import com.example.milestone.dto.MilestoneDto;
import com.example.milestone.service.MilestoneService;
import com.example.task.dto.TaskListWnoSubPidDtoJDJ;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
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

    //마일스톤 목록조회
    @GetMapping("/MilestoneListByPid/{projectId}")
    public List<MilestoneDto> mlist(@PathVariable Integer projectId){
        return milestoneService.getMilestoneListByPid(projectId);
    }

    //마일스톤 탭용 조회
    @GetMapping("/MilestoneTab/{projectId}")
    public List<MilestoneTabDto> mdlist(@PathVariable Integer projectId){
        return milestoneService.getMilestoneTab(projectId);
    }

    //마일스톤 별 하위 프로젝트 / 개별업무 조회
    @GetMapping("/MilestoneSubProjects/{projectId}/{milestoneId}")
    public List<MilestoneSubProjectDto> splist(
            @PathVariable Integer projectId,
            @PathVariable Integer milestoneId){
        return milestoneService.getSubProjectByMid(projectId, milestoneId);
    }

    //마일스톤은 있고 하위프로젝트는 없는 개별업무 조회
    @GetMapping("/MilestoneWnoSubPidList/{projectId}/{milestoneId}")
    public List<TaskListWnoSubPidDtoJDJ> tlistByMid(
            @PathVariable Integer projectId,
            @PathVariable Integer milestoneId){
        return milestoneService.getTaskListWnoSubPid(projectId, milestoneId);
    }
}
