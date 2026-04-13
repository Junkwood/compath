package com.example.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeEntryReportDto {

    //소요시간 테이블
    private Integer timeEntryId;
//    private Integer taskId;
//    private Integer userId;
    private Date    workDate;
    private Integer hours;
    private String  taskDesc;
    private Date    createdAt;

    // 업무
    private Integer taskId;
    private String  title;
    private Integer taskTypeId;
    private Integer projectId;
    private String  projectName;
    private Integer progressRate;
    private Date    startDate;
    private Date    dueDate;


    // 담당자
    private Integer userId;
    private String  userName;

    // 업무유형
    private String  typeName;

    // 집계
    private Integer totalHours;
    private Date    firstWorkDate;
    private Date    lastWorkDate;
    private Integer entryCount;

    private String parentProjectName;  // 상위 프로젝트명
    private String subProjectName;     // 하위 프로젝트명


}
