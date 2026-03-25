package com.example.project.service.impl;

import com.example.project.entity.TestEntity;
import com.example.project.mapper.TestMapper1;
import com.example.project.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestServiceImpl1 implements TestService {
    private final TestMapper1 testMapper;

    @Override
    public TestEntity getNowTime() {
        return testMapper.selectTime();
    }
}