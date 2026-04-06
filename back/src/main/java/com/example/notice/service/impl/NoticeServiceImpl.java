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

    // 공지사항 단건 조회
    @Override
    public NoticeDTO getNoticeById(Integer id) {
        return mapper.getNoticeById(id);
    }

    // 공지사항 수정
    @Override
    public NoticeDTO modifyNotice(NoticeDTO dto) {
        // 수정
        mapper.modifyNotice(dto);

        // 검색할 id 뽑아내기
        int id = dto.getNoticeId();

        return mapper.getNoticeById(id);
    }


}