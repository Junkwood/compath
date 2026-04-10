package com.example.attachment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDTO { // 첨부파일

    private Long attachmentId;

    private String fileName;      // 원본 파일명
    private String filePath;      // 저장된 물리 경로
    private String fileExt;       // 확장자
    private Integer attachmentGroupId; // 그룹 ID (핵심!)

}