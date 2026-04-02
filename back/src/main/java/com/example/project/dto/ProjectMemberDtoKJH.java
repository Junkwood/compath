package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMemberDtoKJH {
    private Integer projectMemberId;
    private Integer userId;
    private Integer projectId;
    private String isActive;

    // 프로젝트 구성원 역할 테이블
    private Integer roleId;
    private Integer projectMemberRoleId;

    // 역할 테이블
    private String roleName;

    // 유저 테이블
    private String userName;
    private String email;
}
