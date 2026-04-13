package com.example.meeting.service.impl;

import com.example.meeting.dto.MeetingAlarmDTO;
import com.example.meeting.dto.MeetingDTO;
import com.example.meeting.mapper.MeetingMapper;
import com.example.meeting.service.MeetingService;
import com.example.task.dto.TaskReqDtoJJW;
import com.example.task.mapper.TaskMapperJJW;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingServiceImpl implements MeetingService {

    private final MeetingMapper mapper;
    private final TaskMapperJJW taskMapperJJW;

    @Override
    public List<MeetingDTO> getMeetingType() {
        return mapper.getMeetingType();
    }

    @Override
    public MeetingDTO registerMeeting(MeetingDTO dto) {

        Integer id = dto.getMeetingLogId();
        if(id != null && id > 0) {
            mapper.modifyNullMeeting(dto);
        } else {
        mapper.registerMeeting(dto);
            id = dto.getMeetingLogId();
        }

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

    // 추천 업무 등록
    @Override
    public List<MeetingDTO> insert(TaskReqDtoJJW taskDto) {
        // 업무 생성
        taskMapperJJW.insert(taskDto);
        int taskId = taskDto.getTaskId();

        // 빈회의록 등록
        MeetingDTO meetDto = new MeetingDTO();
        meetDto.setProjectId(taskDto.getProjectId());
        mapper.registerMeeting(meetDto);

        // 회의록 업무 테이블 등록
        meetDto.setTaskId(taskId);
        mapper.registerMeetingTask(meetDto);

        // 회의록 별 연결 업무 목록 조회
        int id = meetDto.getMeetingLogId();
        return mapper.getRecommandTask(id);
    }

    @Override
    public List<MeetingDTO> removeConnectTask(MeetingDTO dto) {

        // 연결 테이블 삭제
        int connectId = dto.getMeetingtaskId();
        mapper.removeConnectTask(connectId);

        // 연결 테이블 조회(빈회의록 삭제하기위해)
        int meetingId = dto.getMeetingLogId();
        List<MeetingDTO> list = mapper.getRecommandTask(meetingId);

        // null이거나 길이가  0일때 삭제
        if(list != null || !list.isEmpty()){
            mapper.removeNullMeeting(meetingId);
            return new ArrayList<>();
        }

        return list;
    }

}