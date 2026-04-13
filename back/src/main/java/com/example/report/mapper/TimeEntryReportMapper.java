package com.example.report.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;
@Mapper
public interface TimeEntryReportMapper {
    void getTimeEntryReport(Map<String, Object> params);

}
