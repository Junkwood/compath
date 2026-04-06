package com.example.notice.service;

import com.example.notice.dto.NoticeDTO;

public interface NoticeService {

    // 공지사항 등록
    public NoticeDTO registerNotice(NoticeDTO dto);

    //  공지사항 단건 조회
    public NoticeDTO getNoticeById(Integer id);

    //  공지사항 수정
    public NoticeDTO modifyNotice(NoticeDTO dto);
}