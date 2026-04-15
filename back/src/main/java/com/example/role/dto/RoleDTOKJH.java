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
public class RoleDTOKJH {
    private Integer roleId;
    private String roleName;

    private Integer userId;

    private Integer projectId;
    private Integer subProjectId;
}