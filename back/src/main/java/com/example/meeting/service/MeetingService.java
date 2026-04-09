package com.example.meeting.service;

import com.example.meeting.dto.MeetingDTO;

import java.util.List;

public interface MeetingService {
    // 회의록 유형
    public List<MeetingDTO> getMeetingType();
}