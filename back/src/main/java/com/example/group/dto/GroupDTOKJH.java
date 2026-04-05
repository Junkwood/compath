package com.example.group.dto;

import com.example.emp.entity.EmpVOSJW;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTOKJH {
    private Integer userId;
    private String name;
    private Integer groupId;
    private String groupName;
    private String roleName;
    private Integer roleId;
    private String groupType;
}