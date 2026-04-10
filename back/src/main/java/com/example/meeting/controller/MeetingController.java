package com.example.meeting.controller;

import com.example.meeting.dto.MeetingAlarmDTO;
import com.example.meeting.dto.MeetingDTO;
import com.example.meeting.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MeetingController {

    private final MeetingService service;

    //    회의록 유형 조회
    @GetMapping("/meeting/register/typeList")
    public List<MeetingDTO> getMeetingType() {
        return service.getMeetingType();
    }

    //    회의록 등록
    @PostMapping("/meeting/register")
    public MeetingDTO registerMeeting(@RequestBody MeetingDTO dto) {
        return service.registerMeeting(dto);
    }


    // 알림 등록
    @PostMapping("/meeting/alarm/register")
    public int registerCommentAlarm(@RequestBody List<MeetingAlarmDTO> list) {

        return service.registerCommentAlarm(list);
    }

//    회의록 단건 조회
    @GetMapping("/meeting/detail/{id}")
    public MeetingDTO getMeetingById(@PathVariable Integer id){
        return service.getMeetingById(id);
    }

//    회의록 수정
        @PutMapping("/meeting/modify")
    public MeetingDTO modifyMeeting(@RequestBody MeetingDTO dto) {
        return service.modifyMeeting(dto);
    }

//    회의록 필터링 조건 조회
    @GetMapping("/meeting/list")
    public Map<String, Object> getFilter ( MeetingDTO dto) {
        return service.getFilter(dto);
    }
}