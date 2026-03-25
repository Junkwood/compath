package com.example.task.controller;

import com.example.task.entity.TestEntity;
import com.example.task.service.kjhTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class kjhTaskController {
    private final kjhTaskService testService;

    @GetMapping("/test")
    public TestEntity test() {
        return testService.getNowTime();
    }
}