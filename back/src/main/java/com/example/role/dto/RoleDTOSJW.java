package com.example.role.dto;

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
public class RoleDTOSJW {
    private Integer roleId;
    private String roleName;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private Integer editorUserId;
    private Integer userId;
    private String name;
    private String email;
    private String isActive;
    private String userType;
    private List<PermissionDTOSJW> permissions;

}