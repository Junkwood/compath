package com.example.meeting.service.impl;

import com.example.meeting.dto.MeetingAlarmDTO;
import com.example.meeting.dto.MeetingDTO;
import com.example.meeting.mapper.MeetingMapper;
import com.example.meeting.service.MeetingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingServiceImpl implements MeetingService {

    private final MeetingMapper mapper;

    @Override
    public List<MeetingDTO> getMeetingType() {
        return mapper.getMeetingType();
    }

    @Override
    public MeetingDTO registerMeeting(MeetingDTO dto) {

        mapper.registerMeeting(dto);

        int id = dto.getMeetingLogId();

        return mapper.getMeetingById(id);
    }

    @Override
    public int registerCommentAlarm(List<MeetingAlarmDTO> list) {

        MeetingAlarmDTO dto = list.get(0);

        mapper.registerMeetingAlarm(dto);

        int id = dto.getNotificationId();

        Integer result = 0;
        for(int i=1; i<list.size(); i++) {
            list.get(i).setNotificationId(id);

            result += mapper.registerAlarmTarget(list.get(i));
        }

        return result;
    }

    @Override
    public MeetingDTO getMeetingById(Integer id) {
        return mapper.getMeetingById(id);
    }

    @Override
    public MeetingDTO modifyMeeting(MeetingDTO dto) {

        mapper.modifyMeeting(dto);

        int id = dto.getMeetingLogId();
        return mapper.getMeetingById(id);
    }

    @Override
    public Map<String, Object> getFilter(MeetingDTO dto) {

        Map<String, Object> result = new HashMap<>();

        int id = dto.getProjectId();

        List<MeetingDTO> typeList = mapper.getFilterType(id);
        result.put("typeList", typeList);

        List<MeetingDTO> userList = mapper.getFilterUser(id);
        result.put("userList", userList);

        List<MeetingDTO> meetingList = mapper.getAllMeeting(dto);
        result.put("meetingList", meetingList);

        return result;
    }

    @Override
    public List<MeetingDTO> getAllMeeting(MeetingDTO dto) {
        return mapper.getAllMeeting(dto);
    }
}