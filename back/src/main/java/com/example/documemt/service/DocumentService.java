package com.example.documemt.service;

import com.example.documemt.dto.DocumentAlarmDTO;
import com.example.documemt.dto.DocumentCommentDTO;
import com.example.documemt.dto.DocumentDTO;

import java.util.List;
import java.util.Map;

public interface DocumentService {

    // 문서 등록
    public DocumentDTO registerDocument(DocumentDTO dto);

    //  문서 단건 조회
    public Map<String, Object> getDocumentById(Integer id);

    //  문서 수정
    public DocumentDTO modifyDocument(DocumentDTO dto);

    // 문서 필터링들
    public Map<String, Object> getDocumentFilter(DocumentDTO dto);

    // 문서 공지사항 목록
    public List<DocumentDTO> getPagingDocuments(DocumentDTO dto);

    // 문서 댓글 등록
    public List<DocumentCommentDTO> registerComment(DocumentCommentDTO dto);

    //  문서 댓글 수정
    public List<DocumentCommentDTO> modifyComment(DocumentCommentDTO dto);

    // 문서 알림 테이블 등록
    public int registerCommentAlarm(DocumentAlarmDTO dto);
}