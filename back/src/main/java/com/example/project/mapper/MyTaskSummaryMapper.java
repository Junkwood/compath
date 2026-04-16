package com.example.project.mapper;

import com.example.project.dto.MyTaskSummaryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MyTaskSummaryMapper {
    MyTaskSummaryDto getMyTaskSummary(@Param("userId") Integer userId);

}
