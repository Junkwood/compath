package com.example.document.controller;

import com.example.attachment.dto.AttachmentDTO;
import com.example.attachment.service.AttachmentService;
import com.example.document.dto.DocumentAlarmDTO;
import com.example.document.dto.DocumentCommentDTO;
import com.example.document.dto.DocumentDTO;
import com.example.document.service.DocumentService;
import com.example.meeting.dto.MeetingDTO;
import com.example.notice.dto.NoticeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DocumentController {

    private final DocumentService service;
    private final AttachmentService attachmentService;

    // 문서 등록
    @PostMapping("/documents/register")
    public DocumentDTO registerDocument(@RequestPart(value = "files", required = false) List<MultipartFile> files,
                                        @RequestPart("obj") DocumentDTO dto) throws IOException {

        if (files != null && !files.isEmpty() && !files.get(0).isEmpty()) {
            int groupId = 0;
            int id = attachmentService.registerAttachments(files, groupId);
            dto.setAttachmentGroupId(id);
        }

        return service.registerDocument(dto);
    }

    // 문서 조회
    @GetMapping("/documents/Detail/{id}")
    public Map<String, Object> getDocumentById(@PathVariable Integer id) {
        // 결과 담을 그릇
        Map<String, Object> result = new HashMap<>();

        Map<String, Object> info = service.getDocumentById(id);
        result.put("documentInfo", info);

        DocumentDTO detail = (DocumentDTO) info.get("documentInfo");
        Integer groupId = detail.getAttachmentGroupId();

        if(groupId != null) {
            List<AttachmentDTO> list = attachmentService.getFileList(groupId);
            result.put("attachmentList", list);
        }
        return result;
    }

    // 문서 수정
    @PutMapping("/docuemnts/update")
    public DocumentDTO modifyDocument(@RequestPart(value = "files", required = false) List<MultipartFile> files,
                                      @RequestPart("obj") DocumentDTO dto) throws IOException {

        if (files != null && !files.isEmpty() && !files.get(0).isEmpty()) {

            Integer groupId = dto.getAttachmentGroupId();
            if(groupId == null) {
                groupId = 0;
            }
            int id = attachmentService.registerAttachments(files, groupId);

            dto.setAttachmentGroupId(id);
        }

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
    public int registerCommentAlarm(@RequestBody List<DocumentAlarmDTO> list) {

        return service.registerCommentAlarm(list);
    }



}