package com.example.report.service.impl;

import com.example.report.dto.TaskReportDto;
import com.example.report.mapper.TaskReportMapper;
import com.example.report.service.TaskReportService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class TaskReportServiceImpl implements TaskReportService {
    final TaskReportMapper taskReportMapper;

    @Override
    public List<TaskReportDto> getReportAll(TaskReportDto dto) {
        return taskReportMapper.getReportAll(dto);
    }
}
