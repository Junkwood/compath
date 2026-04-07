package com.example.documemt.controller;

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

    // 공지사항 등록
    @PostMapping("/documents/register")
    public DocumentDTO registerDocument(@RequestBody DocumentDTO dto){
        return service.registerDocument(dto);
    }

    // 공지사항 조회
    @GetMapping("/documents/Detail/{id}")
    public DocumentDTO getDocumentById(@PathVariable Integer id) {
        return service.getDocumentById(id);
    }

    // 공지사항 수정
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

//    // 공지사항 비활성
//    @PutMapping("/notice/updateLock/{id}/{isDeleted}")
//    public DocumentDTO modifyNoticeLock(@PathVariable String isDeleted,
//                                        @PathVariable Integer id){
//        return service.modifyNoticeLock(isDeleted, id);
//    }

}