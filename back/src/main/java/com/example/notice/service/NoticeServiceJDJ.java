package com.example.notice.service;

import com.example.notice.dto.NoticeDtoJDJ;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

public interface NoticeServiceJDJ {
    public List<NoticeDtoJDJ> getNoticeListByPid(Integer projectId);
}
