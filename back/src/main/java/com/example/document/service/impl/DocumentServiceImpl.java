package com.example.document.service.impl;

import com.example.document.dto.DocumentAlarmDTO;
import com.example.document.dto.DocumentCommentDTO;
import com.example.document.dto.DocumentDTO;
import com.example.document.mapper.DocumentMapper;
import com.example.document.service.DocumentService;
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

    // 문서 등록
    @Override
    public DocumentDTO registerDocument(DocumentDTO dto) {

        // 문서 등록
        mapper.registerDocument(dto);

        int id = dto.getDocumentId();

        return mapper.getDocumentById(id);
    }

    // 문서 단건 조회
    @Override
    public Map<String, Object> getDocumentById(Integer id) {

        Map<String, Object> result = new HashMap<>();
        result.put("documentInfo", mapper.getDocumentById(id));
        result.put("commentInfo", mapper.getAllComments(id));

        return result;
    }

    // 문서 수정
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
    public List<DocumentCommentDTO> registerComment(DocumentCommentDTO dto) {
        // 댓글 등록
        mapper.registerComment(dto);

        int id = dto.getDocumentId();

        // 문서별 댓글 목록 조회 후 반환
        return mapper.getAllComments(id);
    }

    @Override
    public List<DocumentCommentDTO> modifyComment(DocumentCommentDTO dto) {
        mapper.modifyComment(dto);

        int id = dto.getDocumentId();

        return mapper.getAllComments(id);
    }

    @Override
    public int registerCommentAlarm(List<DocumentAlarmDTO> list) {

        DocumentAlarmDTO dto = list.get(0);

        mapper.registerCommentAlarm(dto);

        int id = dto.getNotificationId();

        Integer result = 0;
        for(int i=1; i<list.size(); i++) {
            list.get(i).setNotificationId(id);

            result += mapper.registerAlarmTarget(list.get(i));
        }

        return result;
    }

}