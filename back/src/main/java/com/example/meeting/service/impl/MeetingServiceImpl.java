package com.example.meeting.service.impl;

import com.example.meeting.dto.MeetingDTO;
import com.example.meeting.mapper.MeetingMapper;
import com.example.meeting.service.MeetingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingServiceImpl implements MeetingService {

    private final MeetingMapper mapper;

    @Override
    public List<MeetingDTO> getMeetingType() {
        return mapper.getMeetingType();
    }
}