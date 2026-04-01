package com.example.role.mapper;

import com.example.role.entity.RoleVOSJW;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapperSJW {
    List<RoleVOSJW> getAll();

}