package com.example.documemt.controller;

import com.example.documemt.dto.DocumentAlarmDTO;
import com.example.documemt.dto.DocumentCommentDTO;
import com.example.documemt.dto.DocumentDTO;
import com.example.documemt.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DocumentController {

    private final DocumentService service;

    // 문서 등록
    @PostMapping("/documents/register")
    public DocumentDTO registerDocument(@RequestBody DocumentDTO dto){
        return service.registerDocument(dto);
    }

    // 문서 조회
    @GetMapping("/documents/Detail/{id}")
    public Map<String, Object> getDocumentById(@PathVariable Integer id) {
        return service.getDocumentById(id);
    }

    // 문서 수정
    @PutMapping("/docuemnts/update")
    public DocumentDTO modifyDocument(@RequestBody DocumentDTO dto){
        return service.modifyDocument(dto);
    }

    // 필터링 조건들
    @GetMapping("/docuemnts/listFilter")
    public Map<String, Object> getDocumentFilter(DocumentDTO dto ) {
        return service.getDocumentFilter(dto);
    }

    // 페이징 목록
    @GetMapping("/docuemnts/listPaging")
    public List<DocumentDTO> getPagingDocuments(DocumentDTO dto ) {
        return service.getPagingDocuments(dto);
    }

    // 문서 댓글 등록
    @PostMapping("/documents/comments/register")
    public List<DocumentCommentDTO> registerComment(@RequestBody DocumentCommentDTO dto){
        return service.registerComment(dto);
    }

    // 댓글 수정
    @PutMapping("/docuemnts/comments/update")
    public List<DocumentCommentDTO> modifyComment(@RequestBody DocumentCommentDTO dto){
        return service.modifyComment(dto);
    }


    // 알림 등록
    @PostMapping("docuemnts/alarm/register")
    public int registerCommentAlarm(@RequestParam(value="arr") List<DocumentAlarmDTO> arr) {
        int result = 0;

        for(DocumentAlarmDTO dto : arr) {
            result = service.registerCommentAlarm(dto);
        }
        return result;
    }



}