package com.example.notice.controller;

import com.example.attachment.dto.AttachmentDTO;
import com.example.attachment.service.AttachmentService;
import com.example.meeting.dto.MeetingDTO;
import com.example.notice.dto.NoticeDTO;
import com.example.notice.service.NoticeService;
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
public class NoticeController {

    private final NoticeService service;
    private final AttachmentService attachmentService;

    // 공지사항 등록
    @PostMapping("/notice/register")
    public NoticeDTO registerNotice(@RequestPart(value = "files", required = false) List<MultipartFile> files,
                                    @RequestPart("obj") NoticeDTO dto)throws IOException{

        if (files != null && !files.isEmpty() && !files.get(0).isEmpty()) {
            int groupId = 0;
            int id = attachmentService.registerAttachments(files, groupId);
            dto.setAttachmentGroupId(id);
        }

        return service.registerNotice(dto);
    }

    // 공지사항 조회
    @GetMapping("/notice/Detail/{id}")
    public Map<String, Object> getNoticeById(@PathVariable Integer id) {
        // 결과 담을 그릇
        Map<String, Object> result = new HashMap<>();

        NoticeDTO info = service.getNoticeById(id);
        result.put("noticeInfo", info);

        Integer groupId = info.getAttachmentGroupId();

        if(groupId != null) {
            List<AttachmentDTO> list = attachmentService.getFileList(groupId);
            result.put("attachmentList", list);
        }

        return result;
    }

    // 공지사항 수정
    @PutMapping("/notice/update")
    public NoticeDTO modifyNotice(@RequestPart(value = "files", required = false) List<MultipartFile> files,
                                  @RequestPart("obj") NoticeDTO dto) throws IOException {

        if (files != null && !files.isEmpty() && !files.get(0).isEmpty()) {

            Integer groupId = dto.getAttachmentGroupId();
            if(groupId == null) {
                groupId = 0;
            }
            int id = attachmentService.registerAttachments(files, groupId);

            dto.setAttachmentGroupId(id);
        }

        return service.modifyNotice(dto);
    }

    // 필터링 조건들
    @GetMapping("/notice/listFilter")
    public Map<String, Object> getNoticeFilter(NoticeDTO dto ) {
        return service.getNoticeFilter(dto);
    }

    // 페이징 목록
    @GetMapping("/notice/listPaging")
    public List<NoticeDTO> getPagingNotice(NoticeDTO dto ) {
        return service.getPagingNotice(dto);
    }

    // 공지사항 비활성
    @PutMapping("/notice/updateLock/{id}/{isDeleted}")
    public NoticeDTO modifyNoticeLock(@PathVariable String isDeleted,
                                      @PathVariable Integer id){
        return service.modifyNoticeLock(isDeleted, id);
    }

}