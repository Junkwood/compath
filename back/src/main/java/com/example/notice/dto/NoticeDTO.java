package com.example.notice.dto;

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
public class NoticeDTO { // 공지사항
    private Integer noticeId;
    private Integer projectId;
    private String title;
    private String content;
    private Integer category;
    private Integer createdBy;
    private String isPinned;
    private String isDeleted;
    private Integer isEditorUserId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;


//    프로젝트 테이블
    private String projectName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd ", timezone = "Asia/Seoul")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDate;

//    역할 테이블
    private Integer taskTypeId;
    private String typeName;

//    유저 테이블
    private String userName;
    private Integer userId;

//    공통코드
    private String pinnedName;

//    페이지네이션
    private Integer taskCounts; //  총 건수
    private Integer startNum = 1;
    private Integer endNum = 10;
    private Integer num;
    private String search;



}