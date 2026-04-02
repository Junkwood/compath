package com.example.group.dto;

import com.example.emp.entity.EmpVOSJW;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupLogsDTOSJW {
    Integer activityLogId;
    String targetType;
    String targetId;
    String actionType;
    Integer actorUserId;
    String actorName;
    String beforeValue;
    String afterValue;
    Date createdAt;
}