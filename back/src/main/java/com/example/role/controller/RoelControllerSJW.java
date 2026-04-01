package com.example.role.controller;

import com.example.role.entity.RoleVOSJW;
import com.example.role.service.RoleServiceSJW;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class RoelControllerSJW {
    private final RoleServiceSJW roleService;

    @GetMapping("/api/role/list")
    public List<RoleVOSJW> getAll() {
        return roleService.getAll();
    }

}