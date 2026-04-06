package com.example.notice.service.impl;

import com.example.notice.dto.NoticeDTO;
import com.example.notice.mapper.NoticeMapper;
import com.example.notice.service.NoticeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper mapper;

    // 공지사항 등록
    @Override
    public NoticeDTO registerNotice(NoticeDTO dto) {

        // 등록된 글 최대 번호 조회
        int id = mapper.getLastNum();
        dto.setNoticeId(id);

        // 공지사항 등록
        mapper.registerNotice(dto);

        return mapper.getNoticeById(id);
    }

    @Override
    public NoticeDTO getNoticeById(Integer id) {
        return mapper.getNoticeById(id);
    }

    //
}