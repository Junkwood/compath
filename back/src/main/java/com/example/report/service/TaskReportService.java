package com.example.report.service;

import com.example.report.dto.TaskReportDto;

import java.util.List;

public interface TaskReportService {
    List<TaskReportDto> getReportAll(TaskReportDto dto);

}
