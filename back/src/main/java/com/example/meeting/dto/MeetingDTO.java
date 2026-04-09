package com.example.meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingDTO { // 회의록
//    회의록 유형(작성시)
    private String typeName;
    private String typeCode;
}