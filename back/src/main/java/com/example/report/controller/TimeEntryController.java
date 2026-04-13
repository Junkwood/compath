package com.example.report.controller;

import com.example.report.service.TimeEntryReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class TimeEntryController {

    private final TimeEntryReportService timeEntryReportService;

    @ResponseBody
    @GetMapping("/time-entry")
    public Map<String, Object> getTimeEntryReport(
            @RequestParam(value = "projectId",      required = false) Integer projectId,
            @RequestParam(value = "assigneeUserId", required = false) Integer assigneeUserId,
            @RequestParam(value = "taskTypeId",     required = false) Integer taskTypeId,
            @RequestParam(value = "keyword",        required = false) String  keyword,
            @RequestParam(value = "dateFrom",       required = false) String  dateFrom,
            @RequestParam(value = "dateTo",         required = false) String  dateTo
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("projectId",      projectId);
        params.put("assigneeUserId", assigneeUserId);
        params.put("taskTypeId",     taskTypeId);
        params.put("keyword",        keyword);
        params.put("dateFrom",       dateFrom);
        params.put("dateTo",         dateTo);

        return timeEntryReportService.getTimeEntryReport(params);
    }
}