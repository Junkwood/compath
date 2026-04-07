package com.example.notice.controller;

import com.example.notice.dto.NoticeDTO;
import com.example.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NoticeController {

    private final NoticeService service;

    // 공지사항 등록
    @PostMapping("/notice/register")
    public NoticeDTO registerNotice(@RequestBody NoticeDTO dto){
        return service.registerNotice(dto);
    }

    // 공지사항 조회
    @GetMapping("/notice/Detail/{id}")
    public NoticeDTO getNoticeById(@PathVariable Integer id) {
        return service.getNoticeById(id);
    }

    // 공지사항 수정
    @PutMapping("/notice/update")
    public NoticeDTO modifyNotice(@RequestBody NoticeDTO dto){
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