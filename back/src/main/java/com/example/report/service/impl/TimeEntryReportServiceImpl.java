package com.example.report.service.impl;

import com.example.report.mapper.TimeEntryReportMapper;
import com.example.report.service.TimeEntryReportService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class TimeEntryReportServiceImpl implements TimeEntryReportService {
    final TimeEntryReportMapper timeEntryReportMapper;

    @Override
    public Map<String, Object> getTimeEntryReport(Map<String, Object> params) {

        params.put("dateFrom", parseDate((String) params.get("dateFrom")));
        params.put("dateTo",   parseDate((String) params.get("dateTo")));

        timeEntryReportMapper.getTimeEntryReport(params);

        Map<String, Object> result = new HashMap<>();
        result.put("reportList",   params.get("reportList"));
        result.put("assigneeList", params.get("assigneeList"));
        result.put("taskTypeList", params.get("taskTypeList"));

        return result;
    }

    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        return Date.from(
                LocalDate.parse(dateStr)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        );
    }
}
