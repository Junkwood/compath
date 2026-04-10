package com.example.notice.controller;

import com.example.notice.dto.NoticeDtoJDJ;
import com.example.notice.service.NoticeServiceJDJ;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NoticeControllerJDJ {

    private final NoticeServiceJDJ noticeServiceJDJ;

    @GetMapping("/notices/getNoticeLists/{projectId}")
    public List<NoticeDtoJDJ> getNoticeListByPid(@PathVariable Integer projectId){
        return noticeServiceJDJ.getNoticeListByPid(projectId);
    }

}
