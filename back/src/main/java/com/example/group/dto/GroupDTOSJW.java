package com.example.group.dto;

import com.example.emp.entity.EmpVOSJW;
import com.example.role.dto.PermissionDTOSJW;
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
public class GroupDTOSJW {
    Integer groupId;
    String groupName;
    String description;
    String roleId;
    Date joinedAt;
    String isActive;
    String groupType;
    List<EmpVOSJW> members;
    List<GroupLogsDTOSJW> logs;
    Integer createdBy;
    Integer editorUserId;
    List<PermissionDTOSJW> permissions;
    private List<Integer> permissionIds;
}