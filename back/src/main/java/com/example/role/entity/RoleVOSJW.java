package com.example.role.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
public class RoleVOSJW {
    private Integer roleId;
    private String roleName;
    private String description;
    private String isActive;
    private Date createAt;
    private Date updateAt;
    private Integer editorUserId;
}
