package com.example.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTOSJW {
    private Integer taskId;
    private Integer projectId;
    private String title;
    private Integer dDay;
    private String priority;      // "긴급", "상", "중", "하"
    private String typeName;
    private String statusName;    // 실제 관리자가 지은 상태 이름 (예: "QA 중", "배포 대기")
    private String isFinal;       // 💡 "O1"(완료), "O2"(진행중) 추가!
    private Integer progressRate;
    private Integer inProgress;
    private Integer done;
    private Integer delayed;
    private Integer pendingPR;
    private Long parentProjectId;
}