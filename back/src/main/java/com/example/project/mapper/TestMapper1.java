package com.example.project.mapper;

import com.example.project.entity.TestEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestMapper1 {
    TestEntity selectTime();
}