//package com.example.log.service;
//
//import com.example.log.dto.ActivityLogDto;
//
//import java.util.List;
//
//public interface ActivityLogService {
//    List<ActivityLogDto> getActivityLogsByProjectId(Integer projectId);
//}

package com.example.log.service;

import com.example.log.dto.ActivityLogDto;
import com.example.log.dto.ActivityLogSearchDto;

import java.util.List;

public interface ActivityLogService {
    List<ActivityLogDto> getActivityLogs(ActivityLogSearchDto searchDto);
}