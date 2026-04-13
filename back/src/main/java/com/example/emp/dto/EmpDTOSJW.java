package com.example.emp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpDTOSJW {
    private Integer userId;
    private String name;
    private String email;
    private String isActive;
    private String userType;
    private String currentPassword;
    private String newPassword;
    private Integer totalUsers;
    private Integer activeProjects;
    private Integer activeUsers;
    private Integer inactiveUsers;
}