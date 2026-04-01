package com.example.role.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTOSJW {
    private Integer userId;
    private String name;
    private String email;
    private String isActive;
    private String userType;
}