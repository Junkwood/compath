package com.example.dajung.service.impl;

import com.example.dajung.entity.TestEntity;
import com.example.dajung.mapper.TestMapper1;
import com.example.dajung.service.TestService;
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