package com.example.document.dto;

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
public class DocumentDTO { // 문서
    private Integer documentId;
    private Integer projectId;
    private Integer parentProjectId;
    private String title;
    private String content;
    private Integer category;
    private Integer createdBy;
    private String isPinned;
    private String isDeleted;
    private Integer isEditorUserId;
    private String isComment;

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
    private String roleName;
    private String roleId;

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
    private Integer count; // 댓글 총 수

//    댓글
    private Integer documentCommentId;
    private String comments;
    private Integer commentWriterId;
    private String commentWriter;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdDay;



}