package com.example.project.dto;

import lombok.Data;

@Data
public class ProjectRolePlListDto {

    //하위프로젝트 생성 시 그 프로젝트의 role이 PL인 사람 불러오기
    private Integer userId;
    private String userName;
    private String roleName;
}
