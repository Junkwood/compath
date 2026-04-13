package com.example.meeting.mapper;

import com.example.meeting.dto.MeetingAlarmDTO;
import com.example.meeting.dto.MeetingDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MeetingMapper {
// 회의록 유형
  public List<MeetingDTO> getMeetingType();

//  회의록 등록
  public int registerMeeting(MeetingDTO dto);

//  회의록 단건 조회
  public MeetingDTO getMeetingById(Integer id);

  // 회의록 알림 테이블 등록
  public int registerMeetingAlarm(MeetingAlarmDTO dto);

  // 회의록 알림 대상테이블 등록
  public int registerAlarmTarget(MeetingAlarmDTO dto);

  // 회의록 알림 테이블 조회
  public int getMeetingAlarmById(Integer targetId);

//  회의록 수정
  public int modifyMeeting(MeetingDTO dto);

//  필터링 조건(유형)
  public List<MeetingDTO> getFilterType(Integer Id);

  //  필터링 조건(작성자)
  public List<MeetingDTO> getFilterUser(Integer Id);

//  페이징 목록
  public List<MeetingDTO> getAllMeeting(MeetingDTO dto);

//    회의록 업무 등록
  public int registerMeetingTask(MeetingDTO dto);

//  회의록 별 연결 업무 조회
  public List<MeetingDTO> getRecommandTask(Integer id);

  //  회의록 연결 업무 연결 해제
  public int removeConnectTask(Integer id);

  //  빈회의록 삭제-
  public int removeNullMeeting(Integer id);

  //  빈회의록 수정
  public int modifyNullMeeting(MeetingDTO dto);

}