package com.example.documemt.service.impl;

import com.example.documemt.dto.DocumentDTO;
import com.example.documemt.mapper.DocumentMapper;
import com.example.documemt.service.DocumentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final DocumentMapper mapper;

    // 공지사항 등록
    @Override
    public DocumentDTO registerDocument(DocumentDTO dto) {

        // 등록된 글 최대 번호 조회
        int id = mapper.getLastNum();
        dto.setDocumentId(id);

        // 공지사항 등록
        mapper.registerDocument(dto);

        return mapper.getDocumentById(id);
    }

    // 공지사항 단건 조회
    @Override
    public DocumentDTO getDocumentById(Integer id) {
        return mapper.getDocumentById(id);
    }

    // 공지사항 수정
    @Override
    public DocumentDTO modifyDocument(DocumentDTO dto) {
        // 수정
        mapper.modifyDocument(dto);

        // 검색할 id 뽑아내기
        int id = dto.getDocumentId();

        return mapper.getDocumentById(id);
    }

    @Override
    public Map<String, Object> getDocumentFilter(DocumentDTO dto) {
        Integer id = dto.getProjectId();

        // 작성자 목록
        List<DocumentDTO> userlist = mapper.getFilterUser(id);

        Map<String, Object> result = new HashMap<>();
        result.put("userList", userlist);

         // 카테고리 목록
        List<DocumentDTO> categorylist = mapper.getFilterCategroy(id);
        result.put("categoryList", categorylist);

        // 공지사항 전체 목록
        List<DocumentDTO> documentlist = mapper.getAllDocuments(dto);
        result.put("documentList", documentlist);

        return result;
    }

    @Override
    public List<DocumentDTO> getPagingDocuments(DocumentDTO dto) {
        return mapper.getAllDocuments(dto);
    }


    @Override
    public DocumentDTO modifyNoticeLock(String isDeleted, Integer noticeId) {

        // 비활성으로 수정
        mapper.modifyNoticeLock(isDeleted, noticeId);


        // 수정 항목 조회
        return mapper.getDocumentById(noticeId);

    }


}