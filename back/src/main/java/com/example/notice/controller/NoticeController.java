package com.example.notice.controller;

import com.example.notice.dto.NoticeDTO;
import com.example.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

}