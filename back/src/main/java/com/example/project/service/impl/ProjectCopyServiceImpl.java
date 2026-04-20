package com.example.project.service.impl;

import com.example.project.dto.ProjectCopyRequestDto;
import com.example.project.mapper.ProjectCopyMapper;
import com.example.project.service.ProjectCopyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectCopyServiceImpl implements ProjectCopyService {

    private final ProjectCopyMapper projectCopyMapper;

    @Override
    public Integer copyProject(ProjectCopyRequestDto dto) {
        Map<String, Object> params = new HashMap<>();

        String copyMembers = dto.getCopyMembers();
        String copySubProjects = dto.getCopySubProjects();
        String copyMilestones = dto.getCopyMilestones();
        String copyTasks = dto.getCopyTasks();
        String useMilestone = dto.getUseMilestone();

        if ("Y".equals(copyTasks)) {
            copySubProjects = "Y";
            copyMilestones = "Y";
        }

        if ("O2".equals(useMilestone) && "Y".equals(copySubProjects)) {
            copyMilestones = "Y";
        }

        params.put("sourceProjectId", dto.getSourceProjectId());
        params.put("projectName", dto.getProjectName());
        params.put("description", dto.getDescription());
        params.put("startDate", dto.getStartDate());
        params.put("endDate", dto.getEndDate());
        params.put("isPublic", dto.getIsPublic());
        params.put("useMilestone", dto.getUseMilestone());
        params.put("pmUserId", dto.getPmUserId());
        params.put("plUserId", dto.getPlUserId());
        params.put("createdBy", dto.getCreatedBy());
        params.put("copyMembers", copyMembers);
        params.put("copySubProjects", copySubProjects);
        params.put("copyMilestones", copyMilestones);
        params.put("copyTasks", copyTasks);

        projectCopyMapper.copyProjectProc(params);

        Object newProjectId = params.get("newProjectId");
        if (newProjectId == null) {
            throw new RuntimeException("프로젝트 복사 실패: 새 프로젝트 ID가 반환되지 않았습니다.");
        }

        return ((Number) newProjectId).intValue();
    }
}