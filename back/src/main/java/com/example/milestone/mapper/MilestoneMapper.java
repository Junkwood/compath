package com.example.milestone.mapper;

import com.example.milestone.dto.MilestoneDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MilestoneMapper {

    //마일스톤 목록조회
    public List<MilestoneDto> getMilestoneListByPid(int projectId);
}
