package com.example.group.entity;

import lombok.Data;

import java.util.Date;

@Data
public class GroupVOSJW {
    String groupId;
    String groupName;
    String groupType;
    String description;
    String isActive;
    Integer createdBy;
    Date createdAt;
    Date updatedAt;
    Integer editorUserId;
    Integer memberCount;
}
