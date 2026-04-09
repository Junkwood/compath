package com.example.notice.service.impl;

import com.example.alarm.service.NotificationService;
import com.example.notice.dto.NoticeDTO;
import com.example.notice.mapper.NoticeMapper;
import com.example.notice.service.NoticeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.jpa.domain.AbstractAuditable_.createdBy;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper mapper;
    private  final NotificationService notificationService;

    // 공지사항 등록
    @Override
    public NoticeDTO registerNotice(NoticeDTO dto) {

        // 등록된 글 최대 번호 조회
        int id = mapper.getLastNum();
        dto.setNoticeId(id);

        // 공지사항 등록
        mapper.registerNotice(dto);


        // 알림 전송
        notificationService.sendToAllProjectMembers(
                dto.getProjectId(),
                "R1", id,
                "공지사항 등록", "새로운 공지사항이 등록되었습니다.",
                dto.getCreatedBy()
        );

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

    @Override
    public Map<String, Object> getNoticeFilter(NoticeDTO dto) {
        Integer id = dto.getProjectId();

        // 작성자 목록
        List<NoticeDTO> userlist = mapper.getFilterUser(id);

        Map<String, Object> result = new HashMap<>();
        result.put("userList", userlist);

         // 카테고리 목록
        List<NoticeDTO> categorylist = mapper.getFilterCategroy(id);
        result.put("categoryList", categorylist);

        // 공지사항 전체 목록
        List<NoticeDTO> noticelist = mapper.getAllNotices(dto);
        result.put("noticeList", noticelist);

        return result;
    }

    @Override
    public List<NoticeDTO> getPagingNotice(NoticeDTO dto) {
        return mapper.getAllNotices(dto);
    }

    @Override
    public NoticeDTO modifyNoticeLock(String isDeleted,Integer noticeId) {

        // 비활성으로 수정
        mapper.modifyNoticeLock(isDeleted, noticeId);


        // 수정 항목 조회
        return mapper.getNoticeById(noticeId);

    }


}