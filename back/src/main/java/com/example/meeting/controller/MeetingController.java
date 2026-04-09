package com.example.meeting.controller;

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

    @GetMapping("/meeting/register/typeList")
    public List<MeetingDTO> getMeetingType(){
        return service.getMeetingType();
    }
}