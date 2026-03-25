package com.example.dajung.mapper;

import com.example.dajung.entity.TestEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestMapper1 {
    TestEntity selectTime();
}