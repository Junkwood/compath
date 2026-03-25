package com.example.dajung.controller;

import com.example.dajung.entity.TestEntity;
import com.example.dajung.service.TestService;
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