package com.example.back.controller;

import com.example.back.entity.TestEntity;
import com.example.back.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;

    @GetMapping("/test")
    public TestEntity test() {
        return testService.getNowTime();
    }
}