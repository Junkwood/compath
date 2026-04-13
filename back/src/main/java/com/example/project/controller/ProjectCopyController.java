package com.example.project.controller;

import com.example.project.dto.ProjectCopyRequestDto;
import com.example.project.service.ProjectCopyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProjectCopyController {


        private final ProjectCopyService projectCopyService;

        @PostMapping("/ProjectCopy")
        public Map<String, Object> copyProject(@RequestBody ProjectCopyRequestDto dto) {
            Integer newProjectId = projectCopyService.copyProject(dto);

            Map<String, Object> result = new HashMap<>();
            result.put("projectId", newProjectId);
            result.put("message", "프로젝트 복사가 완료되었습니다.");
            return result;
        }
    }