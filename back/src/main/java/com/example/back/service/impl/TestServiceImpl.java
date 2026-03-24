package com.example.back.service.impl;

import com.example.back.entity.TestEntity;
import com.example.back.mapper.TestMapper;
import com.example.back.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final TestMapper testMapper;

    @Override
    public TestEntity getNowTime() {
        return testMapper.selectTime();
    }
}