package com.example.emp.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
public class EmpVOSJW implements UserDetails {
    private Integer userId;
    private String name;
    private String email;
    private String password;
    private String isActive;
    private Date createdAt;
    private Date updatedAt;
    private Integer editorUserId;
    private String userType;
    private Integer groupId;
    private List<String> groupList;
    private List<Integer> groupIds;
    private Integer primaryGroupId;
    private String groupName;
    private String isPrimary;
    private Integer groupMemberId;
    private Integer roleId;
    private String roleName;
    private String primaryGroupName;
    private String userName;
    //시큐리티에서 권한 체크
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(()->"ROLE_" + this.userType);
    }
    //시큐리티에서 아이디 체크
    @Override
    public String getUsername() {
        return String.valueOf(this.userId) ;
    }

    public String getUserName() {
        return this.userName;
    }
    //시큐리티에서 비번체크
    @Override
    public String getPassword() {
        return this.password;
    }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
