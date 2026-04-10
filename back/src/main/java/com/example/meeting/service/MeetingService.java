package com.example.meeting.service;

import com.example.meeting.dto.MeetingAlarmDTO;
import com.example.meeting.dto.MeetingDTO;

import java.util.List;
import java.util.Map;

public interface MeetingService {
    // 회의록 유형
    public List<MeetingDTO> getMeetingType();

    //  회의록 등록
    public MeetingDTO registerMeeting(MeetingDTO dto);

    // 회의록 알림 테이블 등록
    public int registerCommentAlarm(List<MeetingAlarmDTO> list);

//    회의록 단건 조회
    public MeetingDTO getMeetingById(Integer id);

    //  회의록 수정
    public MeetingDTO modifyMeeting(MeetingDTO dto);

    //  필터링 조건(유형)
    public Map<String, Object> getFilter(MeetingDTO dto);
}