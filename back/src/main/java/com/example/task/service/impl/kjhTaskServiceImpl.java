package com.example.task.service.impl;

import com.example.task.entity.TestEntity;
import com.example.task.mapper.kjhTaskMapper;
import com.example.task.service.kjhTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class kjhTaskServiceImpl implements kjhTaskService {
    private final kjhTaskMapper testMapper;

    @Override
    public TestEntity getNowTime() {
        return testMapper.selectTime();
    }
}