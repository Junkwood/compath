package com.example.documemt.service;

import com.example.documemt.dto.DocumentDTO;

import java.util.List;
import java.util.Map;

public interface DocumentService {

    // 문서 등록
    public DocumentDTO registerDocument(DocumentDTO dto);

    //  공지사항 단건 조회
    public DocumentDTO getDocumentById(Integer id);

    //  공지사항 수정
    public DocumentDTO modifyDocument(DocumentDTO dto);

    // 공지사항 필터링들
    public Map<String, Object> getDocumentFilter(DocumentDTO dto);

    // 페이지네이션 공지사항 목록
    public List<DocumentDTO> getPagingDocuments(DocumentDTO dto);

    //  공지사항 비활성
    public DocumentDTO modifyNoticeLock(String isDeleted, Integer noticeId);
}