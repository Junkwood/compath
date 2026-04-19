//package com.example.log.mapper;
//
//import com.example.log.dto.ActivityLogDto;
//import org.apache.ibatis.annotations.Mapper;
//
//import java.util.List;
//
//@Mapper
//public interface ActivityLogMapper {
//    List<ActivityLogDto> getActivityLogsByProjectId(Integer projectId);
//}

package com.example.log.mapper;

import com.example.log.dto.ActivityLogDto;
import com.example.log.dto.ActivityLogSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ActivityLogMapper {
    List<ActivityLogDto> getActivityLogs(ActivityLogSearchDto searchDto);
}