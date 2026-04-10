package com.example.notice.service.impl;

import com.example.notice.dto.NoticeDtoJDJ;
import com.example.notice.mapper.NoticeMapperJDJ;
import com.example.notice.service.NoticeServiceJDJ;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeServiceImplJDJ implements NoticeServiceJDJ {

    private final NoticeMapperJDJ noticeMapperJDJ;

    @Override
    public List<NoticeDtoJDJ> getNoticeListByPid(Integer projectId) {
        return noticeMapperJDJ.getNoticeListByPid(projectId);
    }
}
