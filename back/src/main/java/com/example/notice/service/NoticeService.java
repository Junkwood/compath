package com.example.notice.service;

import com.example.notice.dto.NoticeDTO;

import java.util.List;
import java.util.Map;

public interface NoticeService {

    // 공지사항 등록
    public NoticeDTO registerNotice(NoticeDTO dto);

    //  공지사항 단건 조회
    public NoticeDTO getNoticeById(Integer id);

    //  공지사항 수정
    public NoticeDTO modifyNotice(NoticeDTO dto);

    // 공지사항 필터링들
    public Map<String, Object> getNoticeFilter(NoticeDTO dto);

    // 페이지네이션 공지사항 목록
    public List<NoticeDTO> getPagingNotice(NoticeDTO dto);

}