package com.example.project.controller;

import com.example.project.dto.MyTaskSummaryDto;
import com.example.project.service.MyTaskSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MyTaskSummaryController {

    private final MyTaskSummaryService myTaskSummaryService;

    @GetMapping("/project/myTaskSummary/{userId}")
    public MyTaskSummaryDto getMyTaskSummary(@PathVariable Integer userId) {
        return myTaskSummaryService.getMyTaskSummary(userId);
    }
}