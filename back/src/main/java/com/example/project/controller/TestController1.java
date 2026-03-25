package com.example.project.controller;

import com.example.project.entity.TestEntity;
import com.example.project.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController1 {
    private final TestService testService;

    @GetMapping("/test1")
    public TestEntity test() {
        return testService.getNowTime();
    }
}