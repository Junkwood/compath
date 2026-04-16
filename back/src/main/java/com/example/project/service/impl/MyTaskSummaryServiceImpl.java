package com.example.project.service.impl;

import com.example.project.dto.MyTaskSummaryDto;
import com.example.project.mapper.MyTaskSummaryMapper;
import com.example.project.service.MyTaskSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyTaskSummaryServiceImpl implements MyTaskSummaryService {

    private final MyTaskSummaryMapper myTaskSummaryMapper;

    @Override
    public MyTaskSummaryDto getMyTaskSummary(Integer userId) {
        MyTaskSummaryDto result = myTaskSummaryMapper.getMyTaskSummary(userId);

        if (result == null) {
            return new MyTaskSummaryDto(0, 0, 0, 0, 0);
        }

        return result;
    }
}