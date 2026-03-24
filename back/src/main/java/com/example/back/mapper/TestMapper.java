package com.example.back.mapper;

import com.example.back.entity.TestEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestMapper {
    TestEntity selectTime();
}