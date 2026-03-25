package com.example.task.mapper;

import com.example.task.entity.TestEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface kjhTaskMapper {
    TestEntity selectTime();
}