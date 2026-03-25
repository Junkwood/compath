package com.example.emp.entity;

import lombok.Data;

import java.util.Date;

@Data
public class EmpVOSJW {
    private Integer userId;
    private String userName;
    private String email;
    private String password;
    private String isActive;
    private Date createdAt;
    private Date updatedAt;
    private Integer editorUserId;
    private String userType;
}
