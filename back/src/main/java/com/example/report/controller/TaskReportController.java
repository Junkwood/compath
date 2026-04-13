package com.example.report.controller;

import com.example.report.dto.TaskReportDto;
import com.example.report.service.TaskReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TaskReportController {

    final TaskReportService taskReportService;

    @ResponseBody
    @GetMapping("/time-entry-report")
    public List<TaskReportDto> list(@ModelAttribute TaskReportDto dto) {
        return taskReportService.getReportAll(dto);
    }
}