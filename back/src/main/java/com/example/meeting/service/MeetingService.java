package com.example.meeting.service;

import com.example.documemt.dto.DocumentAlarmDTO;
import com.example.meeting.dto.MeetingAlarmDTO;
import com.example.meeting.dto.MeetingDTO;

import java.util.List;

public interface MeetingService {
    // 회의록 유형
    public List<MeetingDTO> getMeetingType();

    //  회의록 등록
    public MeetingDTO registerMeeting(MeetingDTO dto);

    // 회의록 알림 테이블 등록
    public int registerCommentAlarm(List<MeetingAlarmDTO> list);
}