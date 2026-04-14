package com.example.meeting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingDTO { // 회의록
    //    회의록 유형(작성시)
    private String typeName;
    private String typeCode;

    //    회의록 등록시
    private Integer meetingLogId;
    private String title;
    private String content;
    private String meetingTypeCode;
    private String place;
    private Integer projectId;
    private Integer attachmentGroupId;
    private String AiSummary;
    private String sttText;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate meetingDate;

    private Integer createdBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updateAt;

//    회의록 단건 조회
    private String projectName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDate;

    private String userName;

//    회의록 수정
    private Integer editorUserId;

//    추천 업무 등록시(빈 회의록 생성 후 번호로 추천 업무 생성 후 추후 업데이트 하는 방식)
    private Integer meetingtaskId;
    private Integer taskId;
    private String statusName;

//    페이징
    private Integer startNum = 1;
    private Integer endNum = 10;
    private String search;
    private Integer taskCounts;
    private Integer num;

//    참석자 테이블
    private Integer meetingParticipantId;
    private Integer userId;
}