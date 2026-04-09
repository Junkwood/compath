package com.example.meeting.controller;

import com.example.documemt.dto.DocumentAlarmDTO;
import com.example.meeting.dto.MeetingAlarmDTO;
import com.example.meeting.dto.MeetingDTO;
import com.example.meeting.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MeetingController {

    private final MeetingService service;

//    회의록 유형 조회
    @GetMapping("/meeting/register/typeList")
    public List<MeetingDTO> getMeetingType(){
        return service.getMeetingType();
    }

//    회의록 등록
    @PostMapping("/meeting/register")
    public MeetingDTO registerMeeting(@RequestBody MeetingDTO dto){
        return service.registerMeeting(dto);
    }


    // 알림 등록
    @PostMapping("meeting/alarm/register")
    public int registerCommentAlarm(@RequestBody List<MeetingAlarmDTO> list) {

        return service.registerCommentAlarm(list);
    }
}