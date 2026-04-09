package com.example.meeting.mapper;

import com.example.meeting.dto.MeetingDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MeetingMapper {
// 회의록 유형
  public List<MeetingDTO> getMeetingType();
}