package com.example.group.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTOKJH {
    Integer userId;
    String name;
    Integer groupId;
    String groupName;
    String roleName;
}