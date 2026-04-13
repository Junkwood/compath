package com.example.meeting.service;

import com.example.meeting.dto.MeetingAlarmDTO;
import com.example.meeting.dto.MeetingDTO;
import com.example.task.dto.TaskReqDtoJJW;

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

    //  페이징 목록
    public List<MeetingDTO> getAllMeeting(MeetingDTO dto);

    //추천 업무 생성(회의록)
    public List<MeetingDTO> insert(TaskReqDtoJJW dto);

    //  회의록 연결 업무 연결 해제
    public List<MeetingDTO> removeConnectTask(MeetingDTO dto);

}