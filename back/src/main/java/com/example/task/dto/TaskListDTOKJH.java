package com.example.task.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskListDTOKJH {
    private Integer taskId ;
    private Integer parentTaskId;
    private Integer milestoneId;
    private Integer taskTypeId;
    private Integer taskStatusId;
    private String title;
    private String content;
    private Integer assigneeUserId;
    private String priorityCode;
    private Integer progressRate;
    private Integer userId;
    private String roleName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul" )
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul" )
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDate endDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul" )
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDate dueDate;

    private String projectName;
    private Integer projectId;
    private Integer parentProjectId;
    private String typeName;
    private String statusName;
    private String codeName;
    private String userName;

// 백에서 페이지 네이션
    private Integer taskCounts;
    private Integer startNum = 1;
    private Integer endNum = 10;
    private Integer num ;

//  계층형을 위해
    private Integer level;

    private String search;

}