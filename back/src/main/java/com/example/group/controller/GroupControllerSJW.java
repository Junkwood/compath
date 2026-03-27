package com.example.group.controller;

import com.example.group.dto.GroupDTOSJW;
import com.example.group.entity.GroupVOSJW;
import com.example.group.service.GroupServiceSJW;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class GroupControllerSJW {
    private final GroupServiceSJW groupService;

    @GetMapping("/api/group/list")
    public List<GroupVOSJW> getAll() {
        return groupService.getAll();
    }
    @GetMapping("/api/group/list/active")
    public List<GroupDTOSJW> getAllGroupForEmpRegister() {
        return groupService.getAllGroupForEmpRegister();
    }
    @GetMapping("/api/group/info/{id}")
    public GroupVOSJW getById(@PathVariable Integer id) {
        return groupService.getById(id);
    }
    @PostMapping("/api/group")
    public String register(@RequestBody GroupVOSJW emp) {
    return groupService.registerGroup(emp);
    }
}