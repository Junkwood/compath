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
        params.put("copyMembers", dto.getCopyMembers());
        params.put("copyMilestones", dto.getCopyMilestones());
        params.put("copyTasks", dto.getCopyTasks());

        projectCopyMapper.copyProjectProc(params);

        Object newProjectId = params.get("newProjectId");
        if (newProjectId == null) {
            throw new RuntimeException("프로젝트 복사 실패: 새 프로젝트 ID가 반환되지 않았습니다.");
        }

        return ((Number) newProjectId).intValue();
    }
}
